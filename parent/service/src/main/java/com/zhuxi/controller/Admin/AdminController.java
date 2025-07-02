package com.zhuxi.controller.Admin;


import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;
import src.main.java.com.zhuxi.pojo.entity.Role;
import java.util.List;

@RestController
@RequestMapping("/admins")
@Tag(name = "管理端接口")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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


}
