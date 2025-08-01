package com.zhuxi.controller;


import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.business.AdminService;
import com.zhuxi.service.business.UserService;
import com.zhuxi.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminLoginVO;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@Log4j2
public class LoginController {

    private final AdminService adminService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public LoginController(AdminService adminService, JwtUtils jwtUtils, UserService userService) {

        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * 用户登录
     */
    @Operation(
            summary = "用户登录",
            description = "根据交换微信openid登录",
            tags = {"用户端接口"}
    )
    @PostMapping("/user")
    public Result<UserLoginDTO> loginUser(@RequestBody UserLoginDTO userLogin){

        Result<UserLoginDTO> voidResult = userService.loginTest(userLogin);

        if(voidResult.getCode() == 500)
            return voidResult;
        UserLoginDTO data = voidResult.getData();

        Map<String, Object> claims = new HashMap<>();
        claims.put("openId",data.getOpenId());
        claims.put("id",data.getId());
        claims.put("role",data.getRole().name());
        String token = jwtUtils.createToken(claims);
        data.setToken(token);
        return Result.success(Message.LOGIN_SUCCESS,data);
    }

    /**
     * 管理员登录
     */
    @Operation(
            summary = "管理员登录",
            description = "根据提供的账号密码登录",
            tags = {"管理端接口"}
    )
    @PostMapping("/admin")
    public Result<AdminLoginVO> loginAdmin(@RequestBody AdminLoginDTO adminLogin){

        Result<AdminLoginVO> login = adminService.login(adminLogin.getUsername(), adminLogin.getPassword());

        if(login.getCode() == 500)
            return login;

        AdminLoginVO data = login.getData();
        Map<String, Object> claims = new HashMap<String, Object>();

        claims.put("id",data.getId());
        claims.put("username",data.getUsername());

        claims.put("role",data.getRole().name());
        String token = jwtUtils.createToken(claims);
        data.setToken(token);

        return Result.success(Message.LOGIN_SUCCESS,data);
    }
}
