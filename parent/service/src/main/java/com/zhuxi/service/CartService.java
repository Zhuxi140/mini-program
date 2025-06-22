package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CarVO;

import java.util.List;

public interface CartService {


    // 修改购物车商品
    Result<Void> update(CarUpdateDTO carUpdateDTO,String token);

    //加入新的购物车商品
    Result<Void> add(CarUpdateDTO carUpdateDTO,String token);

    //删除购物车商品
    Result<Void> delete(Long productId,String token);

    //获取购物车商品列表
    Result<List<CarVO>> getList(String token);
}
