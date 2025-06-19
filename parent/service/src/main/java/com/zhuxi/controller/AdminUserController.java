package com.zhuxi.controller;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.AdminUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.com.zhuxi.pojo.VO.AdminUserVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/adminUsers")
public class AdminUserController {

    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    @RequireRole(Role.ADMIN)
    public Result<PageResult<AdminUserVO>> getListUser(Integer lastId, @RequestParam(defaultValue = "10") Integer pageSize){

           return userService.getListUser(lastId, pageSize);
       }

}
