package com.zhuxi.ApplicationRunner.Data.Loader;


import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;

import java.util.List;

@Component
public class UserDataLoader {

    private final WechatAuthTxService wechatAuthTxService;
    private final LoginRedisCache loginRedisCache;
    @Value("${init-Data.page-size}")
    private int pageSize;

    public UserDataLoader(WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache) {
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
    }

    public void init(){
        int batchSize = pageSize;
        Long lastId = 0L;
        while (true){
            List<UserBasicDTO> userInfo = wechatAuthTxService.getUserInfo(lastId, batchSize + 1);
            int size = userInfo.size();
            if (size == batchSize + 1){
                lastId = userInfo.get(batchSize).getId();
                userInfo = userInfo.subList(0, batchSize);
                loginRedisCache.initUserInfo(userInfo);
            }else{
                loginRedisCache.initUserInfo(userInfo);
                break;
            }
        }
    }
}
