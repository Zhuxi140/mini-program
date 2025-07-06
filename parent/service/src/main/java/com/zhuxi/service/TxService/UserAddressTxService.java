package com.zhuxi.service.TxService;


import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserAddressDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserAddressVO;

import java.util.List;

@Service
public class UserAddressTxService {

    private final UserAddressMapper userAddressMapper;

    public UserAddressTxService(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }


    @Transactional(readOnly =  true)
    public int  isExist(Long userId){
        return userAddressMapper.isExist(userId);
    }

    @Transactional(readOnly =  true)
    public List<UserAddressVO> getList(Long userId) {
        return userAddressMapper.getList(userId);
    }

    @Transactional(readOnly =  true)
    public Long getDefaultAddressId(Long userId) {
        return userAddressMapper.getDefaultAddressId(userId);
    }


    @Transactional(readOnly =  true)
    public Boolean isExistAddressId(Long addressId) {
        return !userAddressMapper.isExistAddressId(addressId);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void delete(Long addressId) {
        if(!userAddressMapper.delete(addressId))
            throw new transactionalException(Message.DELETE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void update(UserAddressDTO uADto, Long addressId) {
        if(!userAddressMapper.update(uADto,addressId))
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void isDefaultAndUpdateUserAddressId(Long addressId, Long userId) {
        if(userAddressMapper.isDefault(addressId))
            if(userAddressMapper.updateUserAddressId(0L,userId) == 0)
                throw new transactionalException(Message.OPERATION_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertAndUpdateUserAddressId(UserAddressDTO uADto, Long userId){
        if(!userAddressMapper.insert(uADto, userId))
            throw new transactionalException(Message.INSERT_ERROR);
        if(userAddressMapper.updateUserAddressId(uADto.getId(),userId) == 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void cancelDefault(Long defaultAddressId){
        int b = userAddressMapper.cancelDefault(defaultAddressId);
        if(b == 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void setDefaultAndUpdateUserAddressId(Long addressId, Long userId){
        if(userAddressMapper.setDefault(1,addressId) == 0)
            throw new transactionalException(Message.UPDATE_ERROR);
        if(userAddressMapper.updateUserAddressId(addressId,userId) == 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void cancelDefaultAndUpdateUserAddressId(Long addressId, Long userId){
        if(userAddressMapper.cancelDefault(addressId) == 0)
            throw new transactionalException(Message.UPDATE_ERROR);
        if(userAddressMapper.updateUserAddressId(0L,userId) == 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }


}
