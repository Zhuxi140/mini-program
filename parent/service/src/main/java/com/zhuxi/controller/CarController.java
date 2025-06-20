package com.zhuxi.controller;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.CarService;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/car")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * 修改购物车
     * 添加/减少购物车商品数量
     */
    @PutMapping
    @RequireRole(Role.USER)
    public Result<Void> updateCar(@RequestBody CarUpdateDTO carUpdateDTO,
                                  @RequestHeader("Authorization") String token
    ){

        return carService.update(carUpdateDTO,token);
    }

    /**
     * 添加购物车
     */
    @PostMapping
    @RequireRole(Role.USER)
    public Result<Void> addCar(@RequestBody CarUpdateDTO carUpdateDTO,
                               @RequestHeader("Authorization") String token
    ){
        return carService.add(carUpdateDTO, token);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{productId}")
    @RequireRole(Role.USER)
    public Result<Void> deleteCar(@PathVariable Long productId,
                                  @RequestHeader("Authorization") String token
    ){

        return carService.delete(productId, token);
    }
}
