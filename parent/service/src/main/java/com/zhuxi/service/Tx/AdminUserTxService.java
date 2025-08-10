package com.zhuxi.service.Tx;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.zhuxi.pojo.VO.Admin.AdminUserVO;

import java.util.List;

@Service
public class AdminUserTxService {
    private final UserMapper userMapper;

    public AdminUserTxService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public void isUserExist(Long id){
        boolean userExist = userMapper.isUserExistById(id);
        if(!userExist)
            throw new transactionalException(MessageReturn.USER_NOT_EXIST);
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<AdminUserVO> getListUserDESC(Long lastId, Integer pageSize){
        List<AdminUserVO> listUserDESC = userMapper.getListUserDESC(lastId, pageSize);

        if(listUserDESC == null)
            throw new transactionalException(MessageReturn.NO_DATA);

        return listUserDESC;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<AdminUserVO> getListUserASC(Long lastId, Integer pageSize){
        List<AdminUserVO> listUserASC = userMapper.getListUserASC(lastId, pageSize);

        if(listUserASC == null)
            throw new transactionalException(MessageReturn.NO_DATA);

        return listUserASC;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateUserStatus(Integer status,Long id){
        int i = userMapper.updateUserStatus(status, id);
        if(i == 1)
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
    }
}
