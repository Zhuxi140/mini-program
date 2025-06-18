package com.zhuxi.service.Impl;


import com.zhuxi.service.UserService;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.UserLoginVO;
import src.main.java.com.zhuxi.pojo.VO.UserVO;


@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserLoginVO login(String phone) {
        return null;
    }

    @Override
    public UserVO getInfo(String token) {
        return null;
    }
}
