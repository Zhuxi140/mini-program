package com.zhuxi.controller;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.service.business.AdminService;
import com.zhuxi.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminLoginVO;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@Log4j2
public class LoginController {

    private final AdminService adminService;
    private final JwtUtils jwtUtils;

    public LoginController(AdminService adminService, JwtUtils jwtUtils) {

        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
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

        return Result.success(MessageReturn.LOGIN_SUCCESS,data);
    }
}
