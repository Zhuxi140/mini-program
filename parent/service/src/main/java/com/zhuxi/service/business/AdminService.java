package com.zhuxi.service.business;

import com.zhuxi.Result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import com.zhuxi.pojo.VO.Admin.AdminLoginVO;
import com.zhuxi.pojo.VO.Admin.AdminVO;
import com.zhuxi.pojo.entity.Admin;

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


    //登出
    Result<Void> logout(String token, HttpServletRequest request, HttpServletResponse response);


}
