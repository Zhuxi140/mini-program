package com.zhuxi.Interceptor;

import com.zhuxi.ApplicationRunner.Data.Loader.ReBuildBloom;
import com.zhuxi.handler.BloomFilterManager;
import com.zhuxi.handler.BloomLoading;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class UserAccessInterceptor implements HandlerInterceptor {

    private final BloomFilterManager bloomFilterManager;
    private final WechatAuthTxService wechatAuthTxService;
    private final LoginRedisCache loginRedisCache;
    private final ReBuildBloom reBuildBloom;
    private final BloomLoading bloomLoading;


    public UserAccessInterceptor(BloomFilterManager bloomFilterManager, WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache, ReBuildBloom reBuildBloom, BloomLoading bloomLoading) {
        this.bloomFilterManager = bloomFilterManager;
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
        this.reBuildBloom = reBuildBloom;
        this.bloomLoading = bloomLoading;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object jwtOpenid = request.getAttribute("USER_OPENID");
        if (jwtOpenid == null){
            throw new JwtException("token data error");
        }
        String openId = jwtOpenid.toString();
        Long userId = loginRedisCache.getUserId(openId);
        if (userId == null){
            Long userId1 = wechatAuthTxService.getUserId(openId);
            loginRedisCache.saveUserId(openId,userId1);
        }

        if (!bloomFilterManager.mightContainLong("user",userId)) {
            if (bloomLoading.markLoading(userId)){
                reBuildBloom.addUserDat(userId);
                reBuildBloom.addOrderData(userId);
            }
        }
        return true;
    }

}
