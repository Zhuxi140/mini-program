package com.zhuxi.controller.Admin;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.AdminService;
import com.zhuxi.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.zhuxi.pojo.DTO.Admin.AdminLoginDTO;
import com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import com.zhuxi.pojo.VO.Admin.AdminLoginVO;
import com.zhuxi.pojo.VO.Admin.AdminVO;
import com.zhuxi.pojo.entity.Admin;
import com.zhuxi.pojo.entity.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admins")
@Tag(name = "管理端接口")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtils jwtUtils;

    public AdminController(AdminService adminService, JwtUtils jwtUtils) {
        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
    }





    /**
     * 注册管理员接口
     */
    @Operation(
            summary = "注册管理员接口",
            description = "注册管理员接口"
    )
    @PostMapping
    @RequireRole(Role.SUPER_ADMIN)
    public Result<Void> registerAdmin(
            @RequestBody Admin admin
    ){

        return adminService.registerAdmin(admin);
    }


    /**
     * 管理员登录
     */
    @Operation(
            summary = "管理员登录",
            description = "根据提供的账号密码登录"
    )
    @PostMapping("/login")
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


    /**
     * 修改管理员信息接口
     */
    @Operation(
            summary = "修改管理员信息接口",
            description = "至少有一个需要修改的字段"
    )
    @PutMapping
    @RequireRole(Role.SUPER_ADMIN)
    public Result<Void> modifyAdmin(@RequestBody AdminUpdateDTO admin){
        return adminService.updateAdmin(admin);
    }


    /**
     * 根据id获取管理员信息接口
     */
    @Operation(
            summary = "根据id获取管理员信息接口",
            description = "根据id获取管理员信息接口"
    )
    @GetMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<AdminVO> getAdminInfo(
            @Parameter(description = "管理员id", required = true)
            @PathVariable Integer id
    ){
        return adminService.getAdminById(id);
    }


    /**
     * 获取管理员列表接口
     */
    @Operation(
            summary = "获取管理员列表接口",
            description = "获取管理员列表接口"
    )
    @GetMapping
    @RequireRole(Role.ADMIN)
    public Result<List<AdminVO>> getAdminList(){
        return adminService.getAdminList();
    }


    /**
     * 删除管理员接口
     */
    @Operation(
            summary = "删除管理员接口",
            description = "删除管理员接口"
    )
    @DeleteMapping("/{id}")
    @RequireRole(Role.SUPER_ADMIN)
    public Result<Void> deleteAdmin(
            @Parameter(description = "管理员id", required = true)
            @PathVariable Integer id){
        return adminService.deleteAdmin(id);
    }


    @GetMapping("/logout")
    @Operation(
            summary = "管理员登出接口",
            description = "管理员登出接口"
    )
    @RequireRole(Role.ADMIN)
    public Result<Void> logout(
            @Parameter(hidden = true)
            @RequestHeader("Authorization")
            String token,
            @Parameter(hidden = true)
            HttpServletRequest request,
            @Parameter(hidden = true)
            HttpServletResponse response
    ){
        return adminService.logout(token, request, response);
    }


}
