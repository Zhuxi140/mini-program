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
import src.main.java.com.zhuxi.pojo.VO.Order.OrderShowVO;
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


    @PutMapping("/{orderId}")
    @RequireRole(Role.USER)
    @BloomFilterCheck(BloomFilterName = "order",key1 = "orderId",key2 = "userId")
    @Operation(
            summary = "取消订单",
            description = "取消订单"
    )
    public Result<Void> cancelOrder(
            @Parameter(description = "订单id",required = true)
            @PathVariable Long orderId,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.cancelOrder(orderId,userId);
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
    public PageResult<List<OrderRealShowVO>> getOrderList(
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId,
            @Parameter(description = "订单id(第一次不用填)",required = true)
            Long lastId,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "分页大小(默认为10)")
            Integer pageSize
    ){
        return orderService.getOrderList(userId,lastId,pageSize);
    }

    @PutMapping
    @RequireRole(Role.USER)
    @BloomFilterCheck(BloomFilterName = "order",key1 = "orderId",key2 = "userId")
    @Operation(
            summary = "删除订单",
            description = "删除订单"
    )
    public Result<Void> deleteOrder(
            @Parameter(description = "订单id",required = true)
            @RequestParam Long orderId,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return orderService.deleteOrder(orderId,userId);
    }
}
