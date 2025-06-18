package com.zhuxi.controller;


import com.zhuxi.Result.Result;
import com.zhuxi.service.AdminService;
import com.zhuxi.utils.JwtUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.VO.AdminLoginVO;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@Tag(name = "登录接口",description = "用户以及管理员登录接口")
public class LoginController {

    private final AdminService adminService;
    private final JwtUtils jwtUtils;

    public LoginController(AdminService adminService, JwtUtils jwtUtils) {

        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户登录
     */
    @GetMapping("/user")
    public Result loginUser(){

        return Result.success("登录成功");
    }

    /**
     * 管理员登录
     */
    @PostMapping("/admin")
    public Result<AdminLoginVO> loginAdmin(@RequestBody AdminLoginDTO adminLogin){

        Result<AdminLoginVO> login = adminService.login(adminLogin.getUsername(), adminLogin.getPassword());

        if(login.getCode() == 500)
            return login;

        AdminLoginVO data = login.getData();
        Map<String, Object> claims = new HashMap<String, Object>();

        claims.put("id",data.getId());
        claims.put("username",data.getUsername());
        String token = jwtUtils.createToken(claims);
        data.setToken(token);

        return Result.success("登录成功",data);
    }
}
