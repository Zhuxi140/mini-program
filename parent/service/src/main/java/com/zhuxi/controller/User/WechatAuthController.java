package com.zhuxi.controller.User;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.WechatAuthService;
import com.zhuxi.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.User.UserBasicDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserBasicVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.HashMap;

@Slf4j
@RequestMapping("/wechatAuth")
@Tag(name = "微信")
@RestController
public class WechatAuthController {
    private final WechatAuthService wechatAuthService;
    private final JwtUtils jwtUtils;

    public WechatAuthController(WechatAuthService wechatAuthService, JwtUtils jwtUtils) {
        this.wechatAuthService = wechatAuthService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @Operation(summary = "微信登录，返回的name和avatar如果为null 则前端给其固定的的头像和昵称就行")
    public Result<UserBasicVO> login(String code) {
        Result<UserBasicVO> result = wechatAuthService.login(code);
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
    public Result<Void> getUserBasicInfo(@RequestHeader("Authorization") String token,@RequestBody UserBasicDTO userBasicDTO) {
        return wechatAuthService.getUserBasicInfo(token, userBasicDTO);
    }

    @GetMapping("/logout")
    @RequireRole(Role.USER)
    @Operation(summary = "用户登出")
    public Result<Void> logout(@RequestHeader("Authorization") String token, HttpServletRequest request, HttpServletResponse response) {
        log.info("Controller : token : {}", token);
        return wechatAuthService.logout(token, request, response);
    }
}
