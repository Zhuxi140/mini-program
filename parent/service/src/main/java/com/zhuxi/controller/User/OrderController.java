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
}
