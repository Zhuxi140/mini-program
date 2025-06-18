package com.zhuxi.service;




import src.main.java.com.zhuxi.pojo.VO.UserLoginVO;
import src.main.java.com.zhuxi.pojo.VO.UserVO;


public interface UserService {


    // 登录
    UserLoginVO login(String phone);

    // 获取信息
    UserVO getInfo(String token);
}
