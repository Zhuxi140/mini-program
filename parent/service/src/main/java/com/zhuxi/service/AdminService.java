package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.AdminLoginVO;
import src.main.java.com.zhuxi.pojo.VO.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;

import java.util.List;

public interface AdminService {


    Result<AdminLoginVO> login(String username, String password);

    Result<AdminVO> getAdminById(Integer id);

    Result<Void> updateAdmin(AdminVO  admin);

    Result<Void> registerAdmin(Admin  admin);

    Result<List<AdminVO>> getAdminList();

    Result<Void> deleteAdmin(Integer id);
}
