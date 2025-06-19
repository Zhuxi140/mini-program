package com.zhuxi.Interceptor;



import com.zhuxi.Constant.Message;
import com.zhuxi.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;


@Log4j2
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtInterceptorProperties jwtInterceptorProperties;
    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtInterceptorProperties jwtInterceptorProperties, JwtUtils jwtUtils) {
        this.jwtInterceptorProperties = jwtInterceptorProperties;
        this.jwtUtils = jwtUtils;
    }


    /**
     * 前置拦截器 验证JWT
     */
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
            )
    throws Exception {

        String requestURI = request.getRequestURI();

        List<String> excludePaths = jwtInterceptorProperties.getExcludePaths();

        if(excludePaths.contains(requestURI) && request.getMethod().equals("POST"))
        {
            log.info("放行路径: {}", requestURI);
            return true;
        }

        String token = request.getHeader("Authorization");
        if(token !=null && !token.isBlank()){
            token  = token.replaceFirst("(?i)Bearer\\s*", "");
            if(!jwtUtils.verifyToken( token)){
                // 手动回复响应
                response.setStatus(401);
                response.getWriter().write(Message.JWT_ERROR);
                return false;
            }
        }else{
            log.warn("Token is null");
            response.setStatus(401);
            response.getWriter().write(Message.JWT_IS_NULL);
            return false;
        }

        return true;
    }



}
