package com.zhuxi.aspect;


import com.zhuxi.Constant.MessageReturn;
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
import com.zhuxi.pojo.entity.Role;

@Aspect
@Component
@Log4j2
public class RoleCheckAspect {

    /**
     * 角色检查
     */
    @Around("@annotation(requireRole)")
    public  Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {

        HttpServletRequest request = getRequest();
        if (request == null)
            return Result.error(MessageReturn.REQUEST_ERROR);

        Object userRole = request.getAttribute("USER_ROLE");
        Role role;
        try{
            String StrRole = userRole.toString();
            if(StrRole == null || StrRole.isBlank())
                return Result.error(MessageReturn.JWT_NO_ROLE);

            role = Role.valueOf(StrRole.toUpperCase());
            if(role.equals(Role.SUPER_ADMIN) && requireRole.value() != Role.USER)
                return joinPoint.proceed();

        }catch (JwtException e){
            log.warn("Role error :{} ",e.getMessage());
            return Result.error(MessageReturn.ROLE_ERROR);
        }

        if(role.equals(requireRole.value()))
            return joinPoint.proceed();

        return Result.error(MessageReturn.ROLE_ERROR);
    }



    private HttpServletRequest getRequest(){
        ServletRequestAttributes rA = (ServletRequestAttributes )RequestContextHolder.getRequestAttributes();
        return rA == null ? null : (HttpServletRequest) rA.getRequest();
    }

}
