package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;

public interface CarService {


    // 修改购物车商品
    Result<Void> update(CarUpdateDTO carUpdateDTO);
}
