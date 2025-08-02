package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.service.business.CartService;
import com.zhuxi.service.Tx.CartTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartTxService cartTxService;

    public CartServiceImpl(CartTxService cartTxService) {
        this.cartTxService = cartTxService;
    }

    /**
     * 修改购物车商品信息 （增加和减少购物车商品数量以及更换商品规格）
     */
    @Override
    @Transactional
    public Result<Void> update(CartUpdateDTO cartUpdateDTO, Long userId) {

        if(cartUpdateDTO == null || cartUpdateDTO.getCartId() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL + "/" + MessageReturn.CART_ID_IS_NULL);

        if( cartUpdateDTO.getQuantity() == null && cartUpdateDTO.getSpecId() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        cartTxService.updateQuantityOrSpec(cartUpdateDTO,userId);

        return Result.success(MessageReturn.OPERATION_SUCCESS);

    }


    /**
     * 添加购物车商品
     */
    @Override
    @Transactional
    public Result<Void> add(CarAddDTO carAddDTO, Long userId) {

        if(carAddDTO == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        if(carAddDTO.getProductId() == null || carAddDTO.getSpecId() == null)
            return Result.error(MessageReturn.SPEC_ID_IS_NULL + "或" + MessageReturn.PRODUCT_ID_IS_NULL);

        Integer stock = cartTxService.getStock(carAddDTO.getProductId(), carAddDTO.getSpecId());
        if(carAddDTO.getQuantity() > stock)
            return Result.error(MessageReturn.STOCK_NOT_ENOUGH);

        cartTxService.insert(carAddDTO,userId);

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
