package com.zhuxi.controller.User;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Pay.PayDTO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/pay")
@Tag(name = "用户端接口")
public class PayController {
    private final PayService payService;

    public PayController(PayService payService) {
        this.payService = payService;
    }

    @PutMapping
    @RequireRole(Role.USER)
    @Operation(summary = "针对单个商品的支付")
    public Result<Void> pay(
            @RequestBody
            @Parameter(description = "支付信息",required = true)
            PayDTO payDTO,
            @RequestHeader (name = "Authorization")
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ) {
        return payService.pay(payDTO, userId);
    }
}
