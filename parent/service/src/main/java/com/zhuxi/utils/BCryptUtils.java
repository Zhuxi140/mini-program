package com.zhuxi.utils;

import com.zhuxi.utils.properties.BCryptProperties;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
public class BCryptUtils {

    private final BCryptProperties properties;

    public BCryptUtils(BCryptProperties properties){
        this.properties = properties;
    }


    /**
     * 密码加密
     * @param password 密码
     * @return 加密后的密码
     */
    public String hashCode(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(properties.getStrength()));
    }

    /**
     * 密码校验
     * @param password 用户输入的密码
     * @param hashPassword 数据库中的密码
     * @return 返回密码是否一致
     */
    public boolean checkPw(String password,String hashPassword){
        return BCrypt.checkpw(password,hashPassword);
    }
}
