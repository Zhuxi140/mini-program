package com.zhuxi.handler;


import com.zhuxi.annotation.CurrentUserId;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private JwtUtils jwtUtils;

    public UserIdArgumentResolver(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
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
        String token = webRequest.getHeader("Authorization");
        return getUserId( token);
    }

    private Long getUserId(String token)
    {
        Claims claims = jwtUtils.parseToken(token);

        if (claims == null){
            throw new JwtException("token error");
        }
        Long id = claims.get("id", Long.class);
        if (id == null){
            throw new JwtException("token data error");
        }
        return id;
    }


}
