package com.zhuxi.controller;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CarVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Tag(name = "用户端接口")
public class CarController {

    private final CartService cartService;

    public CarController(CartService cartService) {
        this.cartService = cartService;
    }




    /**
     * 修改购物车
     * 添加/减少购物车商品数量
     */
    @PutMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "修改购物车",
            description = "增加/减少购物车商品数量 (仅能操作数量，即只能操作已经加入购物车的商品)"
    )
    public Result<Void> updateCart(

            @RequestBody CarUpdateDTO carUpdateDTO,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){

        return cartService.update(carUpdateDTO,token);
    }

    /**
     * 添加购物车
     */
    @PostMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "添加购物车",
            description = "添加购物车商品，即从第一次加入购物车的商品"
    )
    public Result<Void> addCart(
            @RequestBody CarUpdateDTO carUpdateDTO,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){
        return cartService.add(carUpdateDTO, token);
    }


    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{productId}")
    @RequireRole(Role.USER)
    @Operation(
            summary = "删除购物车商品",
            description = "从购物车中移除的商品,即购物车商品数量从1——>0 "
    )
    public Result<Void> deleteCart(
            @Parameter(description = "商品id", required = true)
            @PathVariable Long productId,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){

        return cartService.delete(productId, token);
    }


    /**
     * 获取购物车列表
     */
    @GetMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取购物车列表",
            description = "获取购物车列表"
    )
    public Result<List<CarVO>> getCartList(
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){
        return cartService.getList(token);
    }

}
