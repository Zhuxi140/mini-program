package com.zhuxi.controller.User;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.List;

@RestController
@RequestMapping("/Order")
@Tag(name = "用户端接口")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "创建单个商品订单",
            description = "创建订单"
    )
    public Result<Void> add(
            @RequestBody OrderAddDTO orderAddDTO,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){
        return orderService.add(orderAddDTO,token);
    }

    @PostMapping("/group")
    @RequireRole(Role.USER)
    @Operation(
            summary = "创建多个商品订单",
            description = "创建订单"
    )
    public Result<Void> addGroup(
            @RequestBody List<OrderAddDTO> orderAddDTO,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){
        return orderService.addGroup(orderAddDTO,token);
    }


    @PutMapping("/{orderId}")
    @RequireRole(Role.USER)
    @Operation(
            summary = "取消订单",
            description = "取消订单"
    )
    public Result<Void> cancelOrder(
            @PathVariable Long orderId,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){
        return orderService.cancelOrder(orderId,token);
    }

    @PutMapping("/group")
    @RequireRole(Role.USER)
    @Operation(
            summary = "取消订单组",
            description = "取消订单组"
    )
    public Result<Void> cancelOrderGroup(
            @RequestParam Long groupId,
            @Parameter(description = "用户token",hidden = true)
            @RequestHeader("Authorization") String token
    ){
        return orderService.cancelOrderGroup(groupId,token);
    }
}
