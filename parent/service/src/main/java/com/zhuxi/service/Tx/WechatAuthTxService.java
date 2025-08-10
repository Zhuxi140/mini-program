package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.WechatServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;
import com.zhuxi.pojo.VO.User.UserBasicVO;

import java.util.List;

@Service
public class WechatAuthTxService {

    private WechatServiceMapper wechatServiceMapper;

    public WechatAuthTxService(WechatServiceMapper wechatServiceMapper) {
        this.wechatServiceMapper = wechatServiceMapper;
    }

    @Transactional(readOnly = true)
    public boolean isExist(String openId){
        int exist = wechatServiceMapper.isExist(openId);
        if (exist > 0){
            return true;
        }
        return false;
    }

    public boolean isBan(String openId){
        int ban = wechatServiceMapper.isBan(openId);
        if (ban == 1){
            return false;
        }else{
            return true;
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void InsertPhone(String phoneNumber, Long userId){
        int insert = wechatServiceMapper.InsertPhone(phoneNumber, userId);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(Long snowflakeId, String openId){
        int insert = wechatServiceMapper.insert(snowflakeId, openId);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertUser(UserBasicDTO userBasicDTO){
        int insert = wechatServiceMapper.insertUser(userBasicDTO);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<UserBasicDTO> getUserInfo(Long lastId, int pageSize){
        List<UserBasicDTO> userInfo = wechatServiceMapper.getUserInfo(lastId, pageSize);
        if (userInfo == null){
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return userInfo;
    }

    @Transactional(readOnly = true)
    public UserBasicVO getUserBasicInfo(String openid,boolean isExist){
        UserBasicVO userBasicInfo = wechatServiceMapper.getUserBasicInfo(openid);
        if(isExist && userBasicInfo == null){
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }

        return userBasicInfo;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getUserId(String openId){
        Long userId = wechatServiceMapper.getUserId(openId);
        if (userId == null){
            throw new transactionalException(MessageReturn.USER_NOT_EXIST);
        }
        return userId;
    }


}
