package com.zhuxi.controller.User;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;
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
     * 添加/减少购物车商品数量和更换规格
     */
    @PutMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "修改购物车",
            description = "增加/减少购物车商品数量 (仅能操作数量和商品规格，即只能操作已经加入购物车的商品)"
    )
    public Result<Void> updateCart(

            @RequestBody CartUpdateDTO cartUpdateDTO,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){

        return cartService.update(cartUpdateDTO,userId);
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
            @RequestBody CartAddDTO cartAddDTO,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return cartService.add(cartAddDTO,userId );
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
            @Parameter(description = "购物车id", required = true)
            Long cartId
    ){

        return cartService.delete(cartId);
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
    public Result<List<CartVO>> getCartList(
            @Parameter(description = "用户token",hidden = true)
            @CurrentUserId Long userId
    ){
        return cartService.getList(userId);
    }


    /**
     * 删除所有购物车商品
     */
    @DeleteMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "删除所有购物车商品",
            description = "删除所有购物车商品"
    )
    public Result<Void> deleteAllCart(
            @Parameter(description = "用户token",hidden = true)
            @CurrentUserId Long userId
    ){
        return cartService.deleteAll(userId);
    }

    @GetMapping("/newProductInfo")
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取购物车商品最新信息",
            description = "用于购物车商品更改规格后，局部获取最新信息"
    )
    public Result<CartNewVO> getNewProductInfo(
            @Parameter(description = "商品id", required = true)
            Long productId,
            @Parameter(description = "商品规格id", required = true)
            Long specId
    ){
        return cartService.getNewCar(productId, specId);
    }

}
