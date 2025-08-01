package com.zhuxi.service.Tx;


import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import java.util.List;

@Service
public class UserTxService {
    private final UserMapper userMapper;

    public UserTxService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Transactional(readOnly = true)
    public UserLoginDTO isUserExist(String openid){

        UserLoginDTO userExist = userMapper.isUserExist(openid);
        if(userExist == null)
            throw new transactionalException(Message.USER_NOT_EXIST);

        return userExist;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Long> getAllUserId(Long lastId,int pageSize){
        return userMapper.getAllUserId(lastId,pageSize);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateUser(UserUpdateDTO userUpdateDTO){
        if(userMapper.updateUser(userUpdateDTO) < 2)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

}
