package com.zhuxi.service.business;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

public interface CartService {


    // 修改购物车商品
    Result<Void> update(CartUpdateDTO cartUpdateDTO, Long userId);

    //加入新的购物车商品
    Result<Void> add(CartAddDTO cartAddDTO, Long userId);

    //删除购物车商品
    Result<Void> delete(Long cartId,Long userId);

    //清空购物车商品
    Result<Void> deleteAll(Long userId);

    //获取购物车商品列表
    Result<PageResult<CartVO, Long>> getList(Long userId, Long lastId, int pageSize);

    //获得局部更新的商品信息
    Result<CartNewVO> getNewCar(Long productId, Long specId);
}
