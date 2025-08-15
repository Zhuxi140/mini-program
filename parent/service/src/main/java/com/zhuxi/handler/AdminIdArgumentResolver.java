package com.zhuxi.handler;


import com.zhuxi.annotation.CurrentAdminId;
import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdminIdArgumentResolver implements HandlerMethodArgumentResolver {

    private JwtUtils jwtUtils;

    public AdminIdArgumentResolver(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public AdminIdArgumentResolver() {
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAdminId.class);
    }

    @Override
    public Map<String, Object> resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception
    {
        String token = webRequest.getHeader("Authorization");
        return getParams( token);
    }

    private Map<String, Object> getParams(String token)
    {
        Claims claims = jwtUtils.parseToken(token);

        if (claims == null){
            throw new JwtException("token error");
        }
        String id = claims.get("id", String.class);
        String userName = claims.get("userName", String.class);
        if (id == null || userName == null || userName.isEmpty()){
            throw new JwtException("token data error");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("userName", userName);
        return data;
    }


}
