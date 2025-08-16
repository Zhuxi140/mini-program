package com.zhuxi.Interceptor;



import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.JwtException;
import com.zhuxi.service.Cache.AdminCache;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;


@Log4j2
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final LoginRedisCache loginRedisCache;
    private final AdminCache adminCache;

    public JwtInterceptor(JwtUtils jwtUtils, LoginRedisCache loginRedisCache, AdminCache adminCache) {
        this.jwtUtils = jwtUtils;
        this.loginRedisCache = loginRedisCache;
        this.adminCache = adminCache;
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
     /*   String requestURI = request.getRequestURI();*/

//        log.info("--------处理url:{}",requestURI);

      /*  List<String> excludePaths = jwtInterceptorProperties.getExcludePaths();*/

        String token = request.getHeader("Authorization");
        if(token !=null && !token.isBlank()){
            token  = token.replaceFirst("(?i)Bearer\\s*", "");
            if(!jwtUtils.verifyToken( token)){
                throw new JwtException(MessageReturn.JWT_ERROR);
            }

        }else{
            log.warn("Token is null");
            throw new JwtException(MessageReturn.JWT_IS_NULL);
        }

        Claims claims = jwtUtils.parseToken(token);
        String id = claims.getId();
        if (id == null){
            throw new JwtException(MessageReturn.JWT_ERROR);
        }
        String tokenValue = loginRedisCache.getTokenValue(token);
        String logOutValue = adminCache.getLogOutValue(token);
        if (tokenValue != null) {
            if (tokenValue.equals(id)) {
                throw new JwtException(MessageReturn.LOGIN_ALREADY_USELESS);
            }
        }
        if (logOutValue != null) {
            if (logOutValue.equals(id)) {
                throw new JwtException(MessageReturn.LOGIN_ALREADY_USELESS);
            }
        }
        // 获取时间戳
        long timestamp = claims.getExpiration().getTime();
        long timeNow = System.currentTimeMillis();
        if( timeNow > timestamp){
            throw new JwtException(MessageReturn.JWT_IS_OVER_TIME);
        }
        String openid = claims.get("openid", String.class);
        Long idd = claims.get("id", Long.class);
        String role = claims.get("role", String.class);
        if (role == null){
            throw new JwtException(MessageReturn.JWT_DATA_ERROR);
        }
        request.setAttribute("ADMIN_ID", idd);
        request.setAttribute("JWT_CLAIMS", claims);
        request.setAttribute("JWT_TOKEN", token);
        request.setAttribute("USER_OPENID", openid);
        request.setAttribute("USER_ROLE", role);
        return true;
    }
}
