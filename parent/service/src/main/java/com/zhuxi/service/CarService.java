package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;

public interface CarService {


    // 修改购物车商品
    Result<Void> update(CarUpdateDTO carUpdateDTO,String token);

    //加入新的购物车商品
    Result<Void> add(CarUpdateDTO carUpdateDTO,String token);

    //删除购物车商品
    Result<Void> delete(Long productId,String token);
}
