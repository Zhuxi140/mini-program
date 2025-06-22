package com.zhuxi.service;




import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserLoginVO;


public interface UserService {


    // 登录(手机号）
    UserLoginVO loginPhone(String phone);

    //登录（测试）
    Result<UserLoginDTO> loginTest(UserLoginDTO userLoginDTO);

    //更新用户个人信息
    Result<Void> updateUserInfo(String token,UserUpdateDTO userUpdateDTO);

}
