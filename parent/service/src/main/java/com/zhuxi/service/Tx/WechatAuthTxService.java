package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.WechatAuthServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserBasicDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserBasicVO;

import java.util.List;

@Service
public class WechatAuthTxService {

    private WechatAuthServiceMapper wechatAuthServiceMapper;

    public WechatAuthTxService(WechatAuthServiceMapper wechatAuthServiceMapper) {
        this.wechatAuthServiceMapper = wechatAuthServiceMapper;
    }

    @Transactional(readOnly = true)
    public boolean isExist(String openId){
        int exist = wechatAuthServiceMapper.isExist(openId);
        if (exist > 0){
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(Long snowflakeId, String openId){
        int insert = wechatAuthServiceMapper.insert(snowflakeId, openId);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertUser(UserBasicDTO userBasicDTO){
        int insert = wechatAuthServiceMapper.insertUser(userBasicDTO);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<UserBasicDTO> getUserInfo(Long lastId, int pageSize){
        List<UserBasicDTO> userInfo = wechatAuthServiceMapper.getUserInfo(lastId, pageSize);
        if (userInfo == null){
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }
        return userInfo;
    }

    @Transactional(readOnly = true)
    public UserBasicVO getUserBasicInfo(String openid,boolean isExist){
        UserBasicVO userBasicInfo = wechatAuthServiceMapper.getUserBasicInfo(openid);
        if(isExist && userBasicInfo == null){
            throw new transactionalException(MessageReturn.SELECT_ERROR);
        }

        return userBasicInfo;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getUserId(String openId){
        Long userId = wechatAuthServiceMapper.getUserId(openId);
        if (userId == null){
            throw new transactionalException(MessageReturn.USER_NOT_EXIST);
        }
        return userId;
    }


}
