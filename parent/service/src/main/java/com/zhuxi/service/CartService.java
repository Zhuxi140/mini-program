package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

public interface CartService {


    // 修改购物车商品
    Result<Void> update(CarUpdateDTO carUpdateDTO,String token);

    //加入新的购物车商品
    Result<Void> add(CarUpdateDTO carUpdateDTO,String token);

    //删除购物车商品
    Result<Void> delete(Long productId,String token,Long specId);

    //清空购物车商品
    Result<Void> deleteAll(String token);

    //获取购物车商品列表
    Result<List<CartVO>> getList(String token);

    //获得局部更新的商品信息
    Result<CartNewVO> getNewCar(Long productId, Long specId);
}
