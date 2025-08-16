package com.zhuxi.handler;


import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private JwtUtils jwtUtils;
    private WechatAuthTxService wechatAuthTxService;
    private LoginRedisCache loginRedisCache;

    public UserIdArgumentResolver(JwtUtils jwtUtils, WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache) {
        this.jwtUtils = jwtUtils;
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
    }

    public UserIdArgumentResolver() {
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class);
    }
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception
    {
        Object jwtOpenid = webRequest.getAttribute("USER_OPENID", RequestAttributes.SCOPE_REQUEST);
        if (jwtOpenid == null){
            throw new JwtException("token data error");
        }
        String openId = jwtOpenid.toString();
        Long userId = loginRedisCache.getUserId(openId);
        if (userId == null){
            Long userId1 = wechatAuthTxService.getUserId(openId);
            loginRedisCache.saveUserId(openId,userId1);
        }
        return userId;
    }
}
