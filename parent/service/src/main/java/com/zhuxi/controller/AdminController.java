package com.zhuxi.controller;


import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.AdminService;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.VO.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;
import src.main.java.com.zhuxi.pojo.entity.Role;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 注册管理员接口
     */
    @PostMapping
    @RequireRole(Role.SUPER_ADMIN)
    public Result<Void> registerAdmin(@RequestBody Admin admin){

        return adminService.registerAdmin(admin);
    }


    /**
     * 修改管理员信息接口
     */
    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> modifyAdmin(@PathVariable Integer id,@RequestBody AdminVO  admin){
        if(!Objects.equals(id, admin.getId()))
            return Result.error(Message.PARAM_ERROR + " or " + Message.BODY_NO_MAIN_OR_IS_NULL);
        return adminService.updateAdmin(admin);
    }


    /**
     * 获取管理员信息接口
     */
    @GetMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<AdminVO> getAdminInfo(@PathVariable Integer id){
        return adminService.getAdminById(id);
    }


    /**
     * 获取管理员列表接口
     */
    @GetMapping
    @RequireRole(Role.ADMIN)
    public Result<List<AdminVO>> getAdminList(){
        return adminService.getAdminList();
    }


    /**
     * 删除管理员接口
     */
    @DeleteMapping("/{id}")
    @RequireRole(Role.SUPER_ADMIN)
    public Result<Void> deleteAdmin(@PathVariable Integer id){
        return adminService.deleteAdmin(id);
    }


}
