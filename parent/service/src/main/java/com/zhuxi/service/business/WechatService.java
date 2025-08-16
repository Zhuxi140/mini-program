package com.zhuxi.service.business;

import com.zhuxi.Result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;
import com.zhuxi.pojo.VO.User.UserBasicVO;

public interface WechatService {

    // 微信登录
    Result<UserBasicVO> login(String code);

    // 获取用户微信信息
    Result<Void> getUserBasicInfo(HttpServletRequest request,UserBasicDTO userBasicDTO);

    //退出登陆
    Result<Void> logout(HttpServletRequest request, HttpServletResponse response);

    //获取用户手机号
    Result<Void> getUserPhone(String code,Long userId);
}
