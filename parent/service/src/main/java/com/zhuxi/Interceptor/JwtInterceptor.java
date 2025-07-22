package com.zhuxi.Interceptor;



import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.JwtException;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;


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
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
            )
    throws Exception {
        String requestURI = request.getRequestURI();

//        log.info("--------处理url:{}",requestURI);

      /*  List<String> excludePaths = jwtInterceptorProperties.getExcludePaths();*/

        String token = request.getHeader("Authorization");
        if(token !=null && !token.isBlank()){
            token  = token.replaceFirst("(?i)Bearer\\s*", "");
            if(!jwtUtils.verifyToken( token)){
                throw new JwtException(Message.JWT_ERROR);
            }

        }else{
            log.warn("Token is null");
            throw new JwtException(Message.JWT_IS_NULL);
        }

        Claims claims = jwtUtils.parseToken(token);
        // 获取时间戳
        long timestamp = claims.getExpiration().getTime();
        long timeNow = System.currentTimeMillis();
        if( timeNow > timestamp){
            throw new JwtException(Message.JWT_IS_OVER_TIME);
        }


        return true;
    }
}
