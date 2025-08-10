package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Cache.CartRedisCache;
import com.zhuxi.service.business.CartService;
import com.zhuxi.service.Tx.CartTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import com.zhuxi.pojo.DTO.Cart.MQdelete;
import com.zhuxi.pojo.VO.Car.CartNewVO;
import com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartTxService cartTxService;
    private final CartRedisCache cartRedisCache;
    private final RabbitTemplate rabbitTemplate;

    public CartServiceImpl(CartTxService cartTxService, CartRedisCache cartRedisCache, RabbitTemplate rabbitTemplate) {
        this.cartTxService = cartTxService;
        this.cartRedisCache = cartRedisCache;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 修改购物车商品信息 （增加和减少购物车商品数量以及更换商品规格）
     */
    @Override
    @Transactional
    public Result<Void> update(CartUpdateDTO cartUpdateDTO, Long userId) {

        if(cartUpdateDTO == null || cartUpdateDTO.getCartId() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL + "/" + MessageReturn.CART_ID_IS_NULL);
        Long specSnowflake = cartUpdateDTO.getSpecSnowflake();
        if( cartUpdateDTO.getQuantity() == null && specSnowflake == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        List<Long> idBySnowflake = cartRedisCache.getIdBySnowflake(specSnowflake);
        Long specId = idBySnowflake.get(1);
        if (specId == null){
            specId = cartTxService.getSpecBySnowFlake(specSnowflake);
            cartRedisCache.saveSpecId(specSnowflake,specId);
        }
        cartUpdateDTO.setSpecId(specId);
        cartTxService.updateQuantityOrSpec(cartUpdateDTO,userId);

        return Result.success(MessageReturn.OPERATION_SUCCESS);

    }


    /**
     * 添加购物车商品
     */
    @Override
    @Transactional
    public Result<Void> add(CartAddDTO cartAddDTO, Long userId) {
        if(cartAddDTO == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        Long specSnowflake = cartAddDTO.getSpecSnowflake();
        if(specSnowflake == null)
            return Result.error(MessageReturn.SPEC_ID_IS_NULL + "或" + MessageReturn.PRODUCT_ID_IS_NULL);

        List<Long> idBySnowflake = cartRedisCache.getIdBySnowflake(cartAddDTO.getSpecSnowflake());
        Long productId = idBySnowflake.get(0);
        Long specId = idBySnowflake.get(1);
        if (productId == null){
            productId = cartTxService.getProductIdBySnowFlake(specSnowflake);
            cartRedisCache.saveProductId(specSnowflake,productId);
        }
        if (specId == null){
            specId = cartTxService.getSpecBySnowFlake(specSnowflake);
            cartRedisCache.saveSpecId(specSnowflake,specId);
        }
        cartAddDTO.setProductId(productId);
        cartAddDTO.setSpecId(specId);

        Integer quantity = cartAddDTO.getQuantity();
        boolean exist = cartTxService.isExist(specId, userId);
        if (!exist){
            cartTxService.insert(cartAddDTO,userId);
            return Result.success(MessageReturn.OPERATION_SUCCESS);
        }
        cartTxService.updateCartStock(specId,userId,quantity);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 删除指定购物车商品
     */
    @Override
    @Transactional
    public Result<Void> delete(Long cartId,Long userId) {

        if(cartId == null)
            return Result.error(MessageReturn.CART_ID_IS_NULL);

        cartTxService.delete(cartId);

        MQdelete mQdelete = new MQdelete(cartId, userId);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED){
                    rabbitTemplate.convertAndSend("cart.exchange", "delete", mQdelete,
                            message -> {
                        MessageProperties props = message.getMessageProperties();
                        props.setMessageId(UUID.randomUUID().toString());
                        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                        }
                            );
                }else{
                    log.warn("事务未提交，截断发送消息---{删除购物车商品---cartId:{}}", cartId);
                }
            }
        });
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 删除所有购物车商品
     */
    @Override
    @Transactional
    public Result<Void> deleteAll(Long userId) {

        cartTxService.deleteAll(userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED){
                    rabbitTemplate.convertAndSend("cart.exchange", "deleteAll", userId,
                            message -> {
                                MessageProperties props = message.getMessageProperties();
                                props.setMessageId(UUID.randomUUID().toString());
                                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                                return message;
                            }
                    );
                }else{
                    log.warn("事务未提交，截断发送消息---{清空购物车商品---userId:{}}", userId);
                }
            }
        });
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 获取购物车商品列表
     */
    @Override
    public Result<PageResult<CartVO, Long>> getList(Long userId, Long lastId, int pageSize) {

        boolean first = (lastId == null || lastId < 0);
        boolean hasMore = false;
        boolean hasPrev;
        hasPrev = !first;

        Set<ZSetOperations.TypedTuple<Object>> cartAllIds = cartRedisCache.getCartAllIds(userId, lastId, pageSize);
        if (cartAllIds != null){
            List<CartVO> cartVOs = cartRedisCache.getCartInfoOptimized(cartAllIds,userId);
            if (cartVOs.size() == pageSize + 1){
                lastId = cartVOs.get(pageSize).getId();
                log.info("lastId:{}", lastId);
                cartVOs = cartVOs.subList(0, pageSize);
                hasMore = true;
            }
            PageResult<CartVO, Long> result = new PageResult<>(cartVOs, lastId, hasPrev, hasMore);
            return Result.success(MessageReturn.OPERATION_SUCCESS,result );
        }

        if (first){
            lastId = Long.MAX_VALUE;
        }
        //未命中
        List<CartVO> cartVO = cartTxService.getListCart(lastId, pageSize + 1, userId);
        if(cartVO.size() == pageSize + 1){
            lastId = cartVO.get(pageSize).getId();
            cartVO = cartVO.subList(0, pageSize);
            hasMore = true;
        }

        for (CartVO cartVO1 : cartVO){
            cartVO1.setUserId(userId);
        }
        rabbitTemplate.convertAndSend("cart.exchange", "lack", cartVO,
                Message->{
                    MessageProperties props = Message.getMessageProperties();
                    props.setMessageId(UUID.randomUUID().toString());
                    props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return Message;
                }
                );

        PageResult<CartVO, Long> result = new PageResult<>(cartVO, lastId, hasPrev, hasMore);
        return Result.success(MessageReturn.OPERATION_SUCCESS, result);
    }



    @Override
    public Result<CartNewVO> getNewCar(Long productId, Long specId) {

        if (productId == null || specId == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL + "或" + MessageReturn.SPEC_ID_IS_NULL);

        CartNewVO cartVO = cartTxService.getNewCar(productId, specId);

        return Result.success(MessageReturn.OPERATION_SUCCESS, cartVO);
    }



}
