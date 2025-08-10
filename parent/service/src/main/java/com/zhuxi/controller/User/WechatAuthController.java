package com.zhuxi.controller.User;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.WechatService;
import com.zhuxi.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;
import com.zhuxi.pojo.VO.User.UserBasicVO;
import com.zhuxi.pojo.entity.Role;

import java.util.HashMap;

@Slf4j
@RequestMapping("/wechatAuth")
@Tag(name = "微信")
@RestController
public class WechatAuthController {
    private final WechatService wechatService;
    private final JwtUtils jwtUtils;

    public WechatAuthController(WechatService wechatService, JwtUtils jwtUtils) {
        this.wechatService = wechatService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @Operation(summary = "微信登录，返回的name和avatar如果为null 则前端给其固定的的头像和昵称就行")
    public Result<UserBasicVO> login(
            @Parameter(description = "微信授权码 前端需调用微信API 获取")
            String code) {
        Result<UserBasicVO> result = wechatService.login(code);
        if (result.getCode() == 500){
            return Result.error(result.getMsg());
        }
        UserBasicVO data = result.getData();
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("openid", data.getOpenid());
        dataMap.put("role","USER");
        String token = jwtUtils.createToken(dataMap);
        data.setToken(token);
        return Result.success(MessageReturn.LOGIN_SUCCESS,data);
    }

    @PostMapping("/getUserBasicInfo")
    @RequireRole(Role.USER)
    @Operation(summary = "后台获取用户基础信息，前端在登录后对应请求用户授权获取微信头像和昵称")
    public Result<Void> getUserBasicInfo(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token,
            @RequestBody UserBasicDTO userBasicDTO) {
        return wechatService.getUserBasicInfo(token, userBasicDTO);
    }

    @GetMapping("/logout")
    @RequireRole(Role.USER)
    @Operation(summary = "用户登出")
    public Result<Void> logout(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token,
            @Parameter(hidden = true)
            HttpServletRequest request,
            @Parameter(hidden = true)
            HttpServletResponse response) {
        log.info("Controller : token : {}", token);
        return wechatService.logout(token, request, response);
    }

    @GetMapping("/getPhone")
    @RequireRole(Role.USER)
    @Operation(summary = "获取用户手机号  前端可在登陆后或者获取完基础信息 后弹窗提示请求用户授权手机号")
    public Result<Void> getPhone(
            @Parameter(hidden = true)
            @CurrentUserId Long userId,
            @Parameter(description = "微信授权码 前端需调用微信API 获取")
            String code) {
        return wechatService.getUserPhone(code,userId);
    }
}
