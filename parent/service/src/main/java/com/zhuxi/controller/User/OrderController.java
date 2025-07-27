package com.zhuxi.controller.User;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.BloomFilterCheck;
import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderAddDTO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderRealShowVO;
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
    public Result<String> add(
            @RequestBody OrderAddDTO orderAddDTO,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.add(orderAddDTO,userId);
    }

    @PostMapping("/group")
    @RequireRole(Role.USER)
    @Operation(
            summary = "创建多个商品订单",
            description = "创建订单"
    )
    public Result<Void> addGroup(
            @RequestBody List<OrderAddDTO> orderAddDTO,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.addGroup(orderAddDTO,userId);
    }


    @PutMapping
    @RequireRole(Role.USER)
    @BloomFilterCheck(BloomFilterName = "order",key1 = "orderId",key2 = "userId")
    @Operation(
            summary = "取消订单",
            description = "取消订单"
    )
    public Result<Void> cancelOrder(
            @Parameter(description = "订单号",required = true)
            String orderSn,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.cancelOrder(orderSn,userId);
    }

    @PutMapping("/group")
    @RequireRole(Role.USER)
    @Operation(
            summary = "取消订单组",
            description = "取消订单组"
    )
    public Result<Void> cancelOrderGroup(
            @Parameter(description = "订单组id",required = true)
            @RequestParam Long groupId,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.cancelOrderGroup(groupId,userId);
    }

    @GetMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取用户订单列表",
            description = "按订单创建时间 由新到旧"
    )
    public Result getOrderList(
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId,
            @Parameter(description = "游标",required = true)
            Long lastScore,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "分页大小(默认为10)")
            Integer pageSize,
            @Parameter(description = "是否nextCursor为false")
            @RequestParam(defaultValue = "false")
            boolean isLast
    ){
        return orderService.getOrderList(userId,lastScore,pageSize,isLast);
    }

    @PutMapping("/delete")
    @RequireRole(Role.USER)
    @BloomFilterCheck(BloomFilterName = "order",key1 = "orderId",key2 = "userId")
    @Operation(
            summary = "删除订单",
            description = "删除订单"
    )
    public Result<Void> deleteOrder(
            @Parameter(description = "订单号",required = true)
            String orderSn,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.deleteOrder(orderSn,userId);
    }
}
