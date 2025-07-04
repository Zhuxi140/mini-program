package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.CartService;
import com.zhuxi.service.TxService.CartTxService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CarVO;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final JwtUtils jwtUtils;
    private final CartTxService cartTxService;

    public CartServiceImpl(CartTxService cartTxService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.cartTxService = cartTxService;
    }

    /**
     * 修改购物车商品信息 （增加和减少购物车商品数量）
     */
    @Override
    @Transactional
    public Result<Void> update(CarUpdateDTO carUpdateDTO,String token) {

        Result<Long> jwtResult = getUserId(token);

        if(jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        if(carUpdateDTO == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        if(carUpdateDTO.getProductId() == null || carUpdateDTO.getQuantity() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        cartTxService.updateQuantity(carUpdateDTO,userId);

        return Result.success(Message.OPERATION_SUCCESS);

    }


    /**
     * 添加购物车商品
     */
    @Override
    @Transactional
    public Result<Void> add(CarUpdateDTO carUpdateDTO,String token) {

        Result<Long> jwtResult = getUserId(token);

        if(jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        if(carUpdateDTO == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        if(carUpdateDTO.getProductId() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        cartTxService.insert(carUpdateDTO,userId);

        return Result.error(Message.OPERATION_SUCCESS);
    }


    /**
     * 删除购物车商品
     */
    @Override
    @Transactional
    public Result<Void> delete(Long productId, String token) {

        Result<Long> jwtResult = getUserId(token);
        if(jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        cartTxService.delete(userId,productId);

        return Result.error(Message.PARAM_ERROR);
    }


    /**
     * 删除所有购物车商品
     */
    @Override
    @Transactional
    public Result<Void> deleteAll(String token) {

        Result<Long> jwtResult = getUserId(token);
        if(jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        cartTxService.deleteAll(userId);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 获取购物车商品列表
     */
    @Override
    public Result<List<CarVO>> getList(String token) {

        if(token == null)
            return Result.error(Message.JWT_IS_NULL);

        Result<Long> jwtResult = getUserId(token);

        if(jwtResult.getCode() != 200)
            return Result.error(jwtResult.getMsg());

        Long userId = jwtResult.getData();
        List<CarVO> carVO = cartTxService.getListCart(userId);

        return Result.success(Message.OPERATION_SUCCESS,carVO);
    }

    private Result<Long> getUserId(String token){
        if (token == null) {
            return Result.error(Message.JWT_IS_NULL);
        }

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null) {
            return Result.error(Message.JWT_ERROR);
        }

        Long userId = claims.get("id", Long.class);
        if (userId == null) {
            return Result.error(Message.JWT_DATA_ERROR);
        }

        return Result.success(userId);
    }


}
