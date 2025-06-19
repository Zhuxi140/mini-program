package com.zhuxi.controller;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }


    /**
     * 修改用户信息
     */
    @PutMapping
    @RequireRole(Role.USER)
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO, @RequestHeader("Authorization") String token){

        return userService.updateUserInfo(token,userUpdateDTO);
    }


}
