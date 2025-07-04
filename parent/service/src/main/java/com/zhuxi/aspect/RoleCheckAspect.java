package com.zhuxi.aspect;


import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import com.zhuxi.annotation.RequireRole;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import src.main.java.com.zhuxi.pojo.entity.Role;

@Aspect
@Component
@Log4j2
public class RoleCheckAspect {
    private final JwtUtils jwtUtils;

    public RoleCheckAspect(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 角色检查
     */
    @Around("@annotation(requireRole)")
    public  Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {

        HttpServletRequest request = getRequest();
        if (request == null)
            return Result.error(Message.REQUEST_ERROR);

        String token = request.getHeader("Authorization");
        if(token == null || token.isBlank())
            return Result.error(Message.USER_NOT_LOGIN + " or " +  Message.JWT_ERROR);

        Claims claims = jwtUtils.parseToken(token);
        if(claims == null){
            log.warn("Role error1 :{} ",claims);
            return Result.error(Message.JWT_ERROR);
        }


        Role role;
        try{
            String StrRole = claims.get("role",String.class);
            if(StrRole == null || StrRole.isBlank())
                return Result.error(Message.JWT_NO_ROLE);

            role = Role.valueOf(StrRole.toUpperCase());

            if(role.equals(Role.SUPER_ADMIN) && requireRole.value() != Role.USER)
                return joinPoint.proceed();

        }catch (JwtException e){
            log.warn("Role error2 :{} ",e.getMessage());
            return Result.error(Message.JWT_ERROR);
        }

        if(role.equals(requireRole.value()))
            return joinPoint.proceed();

        return Result.error(Message.ROLE_ERROR);
    }



    private HttpServletRequest getRequest(){
        ServletRequestAttributes rA = (ServletRequestAttributes )RequestContextHolder.getRequestAttributes();
        return rA == null ? null : (HttpServletRequest) rA.getRequest();
    }

}
