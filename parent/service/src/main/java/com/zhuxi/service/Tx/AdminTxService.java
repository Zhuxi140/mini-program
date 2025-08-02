package com.zhuxi.service.Tx;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.AdminMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminTxService {

    private final AdminMapper adminMapper;

    public AdminTxService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }


    @Transactional(readOnly = true)
    public AdminLoginDTO getPasswordByUsername(String username) {
        AdminLoginDTO passwordByUsername = adminMapper.getPasswordByUsername(username);
        if(passwordByUsername != null)
            return passwordByUsername;

        throw new transactionalException(MessageReturn.USER_NOT_EXIST);
    }


    @Transactional(readOnly = true)
    public void isExists(String username){
        if (adminMapper.isExists(username)) {
            throw new transactionalException(MessageReturn.USER_EXIST);
        }
    }

    @Transactional(readOnly = true)
    public void isExistsById(Integer id){
        if (adminMapper.isExistsById(id)) {
            throw new transactionalException(MessageReturn.USER_EXIST);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public AdminVO getAdminById(Integer id) {
        AdminVO adminById = adminMapper.getAdminById(id);
        if(adminById != null)
            return adminById;

        throw new transactionalException(MessageReturn.USER_NOT_EXIST);
    }

    @Transactional(readOnly = true)
    public List<AdminVO> queryAdminList(){
        List<AdminVO> adminVOS = adminMapper.queryAdminList();
        if(adminVOS != null)
            return adminVOS;

        throw new transactionalException(MessageReturn.NO_DATA);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateLastLogin(Integer id, LocalDateTime lastLogin){
        if (adminMapper.updateLastLogin(id,lastLogin) == 0)
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertAdmin(Admin admin){
        if (!adminMapper.insertAdmin(admin))
            throw new transactionalException(MessageReturn.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void deleteAdmin(Integer id) {
        if (!adminMapper.deleteAdmin(id))
            throw new transactionalException(MessageReturn.DELETE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateAdmin(AdminUpdateDTO  admin){
        if (adminMapper.updateAdmin(admin) == 0)
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
    }
}
