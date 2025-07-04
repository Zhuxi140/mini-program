package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminLoginVO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;

import java.util.List;

public interface AdminService {


    // 登录
    Result<AdminLoginVO> login(String username, String password);

    // 获取管理员信息
    Result<AdminVO> getAdminById(Integer id);

    // 修改管理员信息
    Result<Void> updateAdmin(AdminUpdateDTO admin);

    // 注册
    Result<Void> registerAdmin(Admin  admin);

    // 获取管理员列表
    Result<List<AdminVO>> getAdminList();

    // 删除管理员
    Result<Void> deleteAdmin(Integer id);


}
