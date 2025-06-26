package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.AdminMapper;
import com.zhuxi.service.AdminService;
import com.zhuxi.service.TxService.AdminTxService;
import com.zhuxi.utils.BCryptUtils;
import com.zhuxi.utils.JwtUtils;
import io.micrometer.common.util.StringUtils;
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
import java.util.List;

@Service
@Log4j2
public class AdminServiceImpl implements AdminService {

    private final BCryptUtils bCryptUtils;
    private final AdminTxService adminTxService;

    public AdminServiceImpl(AdminTxService adminTxService, BCryptUtils bCryptUtils, JwtUtils jwtUtils) {
        this.adminTxService = adminTxService;
        this.bCryptUtils = bCryptUtils;
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
            return Result.error(Message.USERNAME_OR_PASSWORD_ERROR);

        }

        AdminLoginVO adminLoginVO = new AdminLoginVO();
        BeanUtils.copyProperties(LoginDTO,adminLoginVO);
        adminLoginVO.setLastLogin(LocalDateTime.now());
        adminTxService.updateLastLogin(adminLoginVO.getId(),adminLoginVO.getLastLogin());

        return Result.success(Message.LOGIN_SUCCESS,adminLoginVO);
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
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

       adminTxService.isExists(admin.getUsername());
        String hashPassword = bCryptUtils.hashCode(admin.getPassword());
        admin.setPassword(hashPassword);
        adminTxService.insertAdmin( admin);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 获取管理员列表
     */
    @Override
    public Result<List<AdminVO>> getAdminList() {
        try {
            List<AdminVO> adminList = adminTxService.queryAdminList();
            if (CollectionUtils.isEmpty(adminList))
                return Result.error(Message.NO_DATA);


            return Result.success(Message.OPERATION_SUCCESS,adminList);
        }catch(Exception e){
            return Result.error(Message.OPERATION_ERROR);
        }
    }


    /**
     * 删除管理员
     */
    @Override
    @Transactional
    public Result<Void> deleteAdmin(Integer id) {
        if(id == null)
            return Result.error(Message.PARAM_ERROR);
       adminTxService.deleteAdmin( id);

       return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 修改管理员信息
     */
    @Override
    @Transactional
    public Result<Void> updateAdmin(AdminUpdateDTO admin) {

        if(admin == null || admin.getId() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        boolean isStatus = admin.getStatus() == null;
        if (isStatus &&  admin.getRole() == null &&
                StringUtils.isBlank(admin.getRealName())) {
            return Result.error(Message.AT_LEAST_ONE_FIELD);
        }

        adminTxService.updateAdmin( admin);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 根据id获取管理员信息
     */
    @Override
    public Result<AdminVO> getAdminById(Integer id) {

        if(id == null)
            return Result.error(Message.PARAM_ERROR);

        AdminVO adminById = adminTxService.getAdminById(id);
        return Result.success(Message.OPERATION_SUCCESS,adminById);
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
