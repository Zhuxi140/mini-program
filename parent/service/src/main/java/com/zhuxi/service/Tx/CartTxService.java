package com.zhuxi.service.Tx;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.CartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import com.zhuxi.pojo.DTO.Cart.CartRedisDTO;
import com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import com.zhuxi.pojo.VO.Car.CartNewVO;
import com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

@Slf4j
@Service
public class CartTxService {

    private final CartMapper cartMapper;

    public CartTxService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Long> getUserIds(Long lastId,int pageSize){
        List<Long> cartIds = cartMapper.getUserIds(lastId, pageSize);
        if(cartIds == null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        return cartIds;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<CartVO> getListCart(Long lastId,int pageSize,Long userId){
        List<CartVO> listCar = cartMapper.getListCar(lastId,pageSize,userId);
        if(listCar == null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return  listCar;
    }
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<CartRedisDTO> getListCartOne(Long userId){
        List<CartRedisDTO> listCarOne = cartMapper.getListCarOne(userId);
        if (listCarOne == null){
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return listCarOne;
    }




    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Integer getStock(Long productId, Long specId){
        Integer stock = cartMapper.getStock(productId, specId);
        if(stock == null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);

        return stock;
    }
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getProductIdBySnowFlake(Long specSnowFlake){
        Long productId = cartMapper.getProductIdBySnowFlake(specSnowFlake);
        if(productId == null || productId < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return productId;
    }


    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getProductSnowFlakeById(Long productId){
        Long ProductSnowFlake = cartMapper.getProductSnowFlakeById(productId);
        if(productId == null || productId < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return ProductSnowFlake;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getCartIdByUS(Long userId, Long specId){
        Long cartIdByUS = cartMapper.getCartIdByUS(userId, specId);
        if(cartIdByUS == null || cartIdByUS < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return cartIdByUS;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getSpecBySnowFlake(Long specSnowFlake){
        Long specId = cartMapper.getSpecIdBySnowFlake(specSnowFlake);
        if(specId == null || specId < 0) {
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return specId;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public CartNewVO getNewCar(Long productId, Long specId){
        CartNewVO newCar = cartMapper.getNewCar(productId, specId);
        if(newCar == null)
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        return newCar;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateQuantityOrSpec(CartUpdateDTO cartUpdateDTO, Long userId){
        int i = cartMapper.updateQuantityOrSpec(cartUpdateDTO,userId);
        if(i < 1)
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(CartAddDTO cartAddDTO, Long userId){
        Integer sumCount = cartMapper.getSumCount(userId);
        if (sumCount >= 300){
            throw new transactionalException(MessageReturn.CART_IS_OVER_LIMIT);
        }
        if(!cartMapper.insert(cartAddDTO, userId))
            throw new transactionalException(MessageReturn.INSERT_ERROR);
    }


    @Transactional(rollbackFor = transactionalException.class)
    public void updateCartStock(Long sepcId, Long userId, int quantity){
        int i = cartMapper.updateCartStock(sepcId, userId, quantity);
        if (i < 1){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public boolean isExist(Long sepcId, Long userId){
        Integer cartCount = cartMapper.getCartCount(sepcId, userId);
        if (cartCount == null || cartCount <= 0){
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void delete(Long cartId){
        int delete = cartMapper.delete(cartId);
        if (delete == 0 )
            throw new transactionalException(MessageReturn.CART_NO_RECORD_OR_ERROR);
    }


    @Transactional(rollbackFor = transactionalException.class)
    public void deleteAll(Long userId){
        if(!cartMapper.deleteAll(userId))
            throw new transactionalException(MessageReturn.CART_NO_RECORD_OR_ERROR);
    }
}
