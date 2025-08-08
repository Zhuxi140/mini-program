package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Cache.AdminCache;
import com.zhuxi.service.business.AdminService;
import com.zhuxi.service.Tx.AdminTxService;
import com.zhuxi.utils.BCryptUtils;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminLoginVO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class AdminServiceImpl implements AdminService {

    private final BCryptUtils bCryptUtils;
    private final AdminTxService adminTxService;
    private final JwtUtils jwtUtils;
    private final AdminCache adminCache;

    public AdminServiceImpl(AdminTxService adminTxService, BCryptUtils bCryptUtils, JwtUtils jwtUtils, JwtUtils jwtUtils1, AdminCache adminCache) {
        this.adminTxService = adminTxService;
        this.bCryptUtils = bCryptUtils;
        this.jwtUtils = jwtUtils1;
        this.adminCache = adminCache;
    }

    /**
     * 登录
     */
    @Override
    @Transactional
    public Result<AdminLoginVO> login(String username, String password) {

        AdminLoginDTO LoginDTO = adminTxService.getPasswordByUsername(username);

        String vPassword = AdminServiceImpl.getRandomDummyHash();
        String hashPassword = LoginDTO != null ? LoginDTO.getPassword() : vPassword;

        if(LoginDTO == null || !bCryptUtils.checkPw(password,hashPassword)){
            log.error("用户名或密码错误,{},{}",hashPassword,password);
            return Result.error(MessageReturn.USERNAME_OR_PASSWORD_ERROR);

        }

        AdminLoginVO adminLoginVO = new AdminLoginVO();
        BeanUtils.copyProperties(LoginDTO,adminLoginVO);
        adminLoginVO.setLastLogin(LocalDateTime.now());
        adminTxService.updateLastLogin(adminLoginVO.getId(),adminLoginVO.getLastLogin());

        return Result.success(MessageReturn.LOGIN_SUCCESS,adminLoginVO);
    }


    /**
     * 退出
     */


    /**
     *  注册
     */
    @Override
    @Transactional
    public Result<Void> registerAdmin(Admin admin) {

        if(admin == null || StringUtils.isBlank(admin.getUsername()) || StringUtils.isBlank(admin.getPassword()))
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

       adminTxService.isExists(admin.getUsername());
        String hashPassword = bCryptUtils.hashCode(admin.getPassword());
        admin.setPassword(hashPassword);
        adminTxService.insertAdmin( admin);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 获取管理员列表
     */
    @Override
    public Result<List<AdminVO>> getAdminList() {
        try {
            List<AdminVO> adminList = adminTxService.queryAdminList();
            if (CollectionUtils.isEmpty(adminList))
                return Result.error(MessageReturn.NO_DATA);


            return Result.success(MessageReturn.OPERATION_SUCCESS,adminList);
        }catch(Exception e){
            return Result.error(MessageReturn.OPERATION_ERROR);
        }
    }


    /**
     * 删除管理员
     */
    @Override
    @Transactional
    public Result<Void> deleteAdmin(Integer id) {
        if(id == null)
            return Result.error(MessageReturn.PARAM_ERROR);
        adminTxService.isExistsById(id);
       adminTxService.deleteAdmin( id);

       return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    @Override
    public Result<Void> logout(String token, HttpServletRequest request, HttpServletResponse response) {

        Claims claims = jwtUtils.parseToken(token);
        Date expiration = claims.getExpiration();
        String jit = claims.getId();
        long ttl = TimeUnit.MILLISECONDS.toSeconds(expiration.getTime() - System.currentTimeMillis());
        if (ttl > 0){
            adminCache.saveLogOutToken(token,jit,ttl,TimeUnit.SECONDS);
        }

        HttpSession session = request.getSession(false);
        if (session != null){
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            Arrays.stream( cookies)
                    .filter(cookie->"AUTH_TOKEN".equals(cookie.getName()) || "JSESSIONID".equals(cookie.getName()))
                    .forEach(cookie-> {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    });
        }
        return Result.success(MessageReturn.LOGOUT_SUCCESS);
    }

    /**
     * 修改管理员信息
     */
    @Override
    @Transactional
    public Result<Void> updateAdmin(AdminUpdateDTO admin) {

        if(admin == null || admin.getId() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        boolean isStatus = admin.getStatus() == null;
        if (isStatus &&  admin.getRole() == null &&
                StringUtils.isBlank(admin.getRealName())) {
            return Result.error(MessageReturn.AT_LEAST_ONE_FIELD);
        }
        adminTxService.isExistsById(admin.getId());
        adminTxService.updateAdmin( admin);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 根据id获取管理员信息
     */
    @Override
    public Result<AdminVO> getAdminById(Integer id) {

        if(id == null)
            return Result.error(MessageReturn.PARAM_ERROR);
        adminTxService.isExistsById( id);
        AdminVO adminById = adminTxService.getAdminById(id);
        return Result.success(MessageReturn.OPERATION_SUCCESS,adminById);
    }



    public static String getRandomDummyHash() {
        return DUMMY_HASHES.get(new SecureRandom().nextInt(DUMMY_HASHES.size()));
    }

    private static final List<String> DUMMY_HASHES = Arrays.asList(
            "$2a$10$r0euLqK2/F1TYHJXdQR1d.0CMxLPSGfUi5n.tozT6ci0np4vSRkY2",
            "$2a$10$m61vl5MOo6Z6WBjvOmGUe.LDg.pjkkcKMD5QwoOmVUrHGlcxx2WXa",
            "$2a$10$MVxuj.10kan9WznQyxSFYe.Hq7ywAuke/O6HNxYTjL0JLzcTOm2m6",
            "$2a$10$5zt9O/YOb24hr72JjeRxYO0uId2xTCXBxKw1e1o1H7Ok3iVLl1uAe"
    );
}
