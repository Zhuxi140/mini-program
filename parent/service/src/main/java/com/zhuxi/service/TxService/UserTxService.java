package com.zhuxi.service.TxService;


import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;

@Service
public class UserTxService {
    private final UserMapper userMapper;

    public UserTxService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public UserLoginDTO isUserExist(String openid){

        UserLoginDTO userExist = userMapper.isUserExist(openid);
        if(userExist == null)
            throw new transactionalException(Message.USER_NOT_EXIST);

        return userExist;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateUser(UserUpdateDTO userUpdateDTO){
        if(userMapper.updateUser(userUpdateDTO) < 2)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

}
