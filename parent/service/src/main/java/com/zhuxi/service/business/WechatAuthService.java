package com.zhuxi.service.business;

import com.zhuxi.Result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.main.java.com.zhuxi.pojo.DTO.User.UserBasicDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserBasicVO;

public interface WechatAuthService {

    // 微信登录
    Result<UserBasicVO> login(String code);

    // 获取用户微信信息
    Result<Void> getUserBasicInfo(String userId, UserBasicDTO userBasicDTO);

    //退出登陆
    Result<Void> logout(String token, HttpServletRequest request, HttpServletResponse response);
}
