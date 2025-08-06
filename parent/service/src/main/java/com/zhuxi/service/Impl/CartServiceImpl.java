package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Cache.CartRedisCache;
import com.zhuxi.service.business.CartService;
import com.zhuxi.service.Tx.CartTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartTxService cartTxService;
    private final CartRedisCache cartRedisCache;

    public CartServiceImpl(CartTxService cartTxService, CartRedisCache cartRedisCache) {
        this.cartTxService = cartTxService;
        this.cartRedisCache = cartRedisCache;
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
            return Result.error(MessageReturn.OPERATION_SUCCESS);
        }
        cartTxService.updateCartStock(specId,userId,quantity);

        return Result.error(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 删除指定购物车商品
     */
    @Override
    @Transactional
    public Result<Void> delete(Long cartId) {

        if(cartId == null)
            return Result.error(MessageReturn.CART_ID_IS_NULL);

        cartTxService.delete(cartId);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 删除所有购物车商品
     */
    @Override
    @Transactional
    public Result<Void> deleteAll(Long userId) {

        cartTxService.deleteAll(userId);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 获取购物车商品列表
     */
    @Override
    public Result<List<CartVO>> getList(Long userId) {

        List<CartVO> cartVO = cartTxService.getListCart(userId);

        return Result.success(MessageReturn.OPERATION_SUCCESS, cartVO);
    }


    @Override
    public Result<CartNewVO> getNewCar(Long productId, Long specId) {

        if (productId == null || specId == null)
            return Result.error(MessageReturn.PRODUCT_ID_IS_NULL + "或" + MessageReturn.SPEC_ID_IS_NULL);

        CartNewVO cartVO = cartTxService.getNewCar(productId, specId);

        return Result.success(MessageReturn.OPERATION_SUCCESS, cartVO);
    }



}
