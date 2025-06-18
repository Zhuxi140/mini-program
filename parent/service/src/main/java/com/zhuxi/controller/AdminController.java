package com.zhuxi.controller;


import com.zhuxi.Result.Result;
import com.zhuxi.service.AdminService;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.VO.AdminVO;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 修改管理员信息接口
     */
    @PostMapping("/modify")
    public Result modifyAdmin(@RequestBody AdminVO  admin){
        return adminService.updateAdmin(admin);
    }

    /**
     * 获取管理员信息接口
     */
    @GetMapping("/getAdminInfo")
    public Result<AdminVO> getAdminInfo(@RequestParam Integer id){
        return adminService.getAdminById(id);
    }

    @GetMapping("/getAdminList")
    public Result<List<AdminVO>> getAdminList(){
        return adminService.getAdminList();
    }
}
