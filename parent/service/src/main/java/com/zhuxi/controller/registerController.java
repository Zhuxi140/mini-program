package com.zhuxi.controller;


import com.zhuxi.Result.Result;
import com.zhuxi.service.AdminService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.com.zhuxi.pojo.entity.Admin;

@RestController
@RequestMapping("/register")
public class registerController {
    private final AdminService adminService;

    public registerController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/admin")
    public Result registerAdmin(@RequestBody Admin admin){

        return adminService.registerAdmin(admin);
    }

}
