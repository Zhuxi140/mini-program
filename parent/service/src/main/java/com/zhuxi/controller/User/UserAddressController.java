package com.zhuxi.controller.User;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.zhuxi.pojo.DTO.User.UserAddressDTO;
import com.zhuxi.pojo.VO.User.UserAddressVO;
import com.zhuxi.pojo.entity.Role;

import java.util.List;

@RestController
@RequestMapping("/userAddress")
@Tag(name = "用户端接口")
public class UserAddressController {
    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }


    /**
     * 添加用户地址接口
     */
    @PostMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "添加用户地址",
            description = "添加用户地址"
    )
    public Result<Void> addUserAddress(
            @RequestBody UserAddressDTO userAddressDTO,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return userAddressService.add(userAddressDTO,userId);
    }



    /**
     * 设置用户地址为默认接口
     */
    @PutMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "设置用户地址为默认接口",
            description = "设置用户地址为默认接口"
    )
    public Result<Void> setDefaultUserAddress(
            @Parameter(description = "用户地址id",required = true)
            Long addressId,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return userAddressService.setDefault(addressId,userId);
    }



    /**
     * 取消用户地址为默认接口
     */
    @PutMapping("/{addressId}")
    @RequireRole(Role.USER)
    @Operation(
            summary = "取消用户地址为默认接口",
            description = "取消用户地址为默认接口"
    )
    public Result<Void> cancelDefaultUserAddress(
            @Parameter(description = "用户地址id",required = true)
            @PathVariable Long addressId,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return userAddressService.cancelDefault(addressId,userId);
    }


    /**
     * 获取用户地址列表接口
     */
    @GetMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取用户地址列表",
            description = "获取用户地址列表"
    )
    public Result<List<UserAddressVO>> getUserAddressList(
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return userAddressService.getList(userId);
    }


    /**
     * 删除用户地址接口
     */
    @DeleteMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "删除用户地址",
            description = "删除用户地址"
    )
    public Result<Void> deleteUserAddress(
            @Parameter(description = "用户地址id",required = true)
            Long addressId,
            @Parameter(description = "用户id",hidden = true)
            @CurrentUserId Long userId
    ){
        return userAddressService.delete(addressId, userId);
    }

    /**
     * 修改用户地址接口
     */
    @PutMapping("/update")
    @RequireRole(Role.USER)
    @Operation(
            summary = "修改用户地址信息",
            description = "修改用户地址信息"
    )
    public Result<Void> updateUserAddress(
            @RequestBody UserAddressDTO userAddressDTO,
            @Parameter(description = "地址id",required = true)
            Long addressId,
            @Parameter(description ="用户id",hidden = true )
            @CurrentUserId Long userId
    ){
        return userAddressService.update(userAddressDTO,addressId,userId);
    }
}
