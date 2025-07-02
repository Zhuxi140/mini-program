package com.zhuxi.controller.User;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/users")
@Tag(name = "用户端接口")
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
    @Operation(
            summary = "修改用户信息",
            description = "至少修改一个字段"
    )
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO,
                                       @Parameter(description = "用户token",hidden = true)
                                       @RequestHeader("Authorization") String token){

        return userService.updateUserInfo(token,userUpdateDTO);
    }


}
