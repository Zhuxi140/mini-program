package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.TxService.UserAddressTxService;
import com.zhuxi.service.UserAddressService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserAddressDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserAddressVO;

import java.util.List;


@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressTxService userAddressTxService;

    public UserAddressServiceImpl(UserAddressTxService userAddressTxService) {
        this.userAddressTxService = userAddressTxService;
    }

    /**
     * 添加用户地址
     */
    @Override
    @Transactional
    public Result<Void> add(UserAddressDTO userAddressDTO, Long userId) {

        int exist = userAddressTxService.isExist(userId);

        if(exist == 5)
            return Result.error(Message.USER_ADDRESS_MAX);
        else if(exist == 0){
            userAddressDTO.setIsDefault(1);
            Long defaultAddressId = userAddressTxService.getDefaultAddressId(userId);

            if(defaultAddressId != null)
                userAddressTxService.cancelDefault(defaultAddressId);
        }


        userAddressTxService.insertAndUpdateUserAddressId(userAddressDTO,userId);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**Long userId地址为默认
     */
    @Override
    @Transactional
    public Result<Void> setDefault(Long addressId, Long userId) {

        if (addressId == null || userAddressTxService.isExistAddressId(addressId))
            return Result.error(Message.PARAM_ERROR + " 或 " + Message.USER_ADDRESS_NOT_EXIST);

        Long defaultAddressId = userAddressTxService.getDefaultAddressId(userId);

        if(defaultAddressId != null)
            userAddressTxService.cancelDefault(defaultAddressId);

        userAddressTxService.setDefaultAndUpdateUserAddressId(addressId,userId);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 取消默认地址
     */
    @Override
    @Transactional
    public Result<Void> cancelDefault(Long addressId,Long userId) {

        if(addressId == null)
            return Result.error(Message.PARAM_ERROR);

        userAddressTxService.cancelDefaultAndUpdateUserAddressId(addressId,userId);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 获取用户地址列表
     */
    @Override
    public Result<List<UserAddressVO>> getList(Long userId) {

        List<UserAddressVO> list = userAddressTxService.getList(userId);
        if(list != null)
            return Result.success(Message.OPERATION_SUCCESS,list);

        return Result.error(Message.NO_USER_ADDRESS);
    }

    /**
     * 删除用户地址
     */
    @Override
    @Transactional
    public Result<Void> delete(Long addressId,Long userId) {

        if(addressId == null)
            return Result.error(Message.PARAM_ERROR);


        userAddressTxService.isDefaultAndUpdateUserAddressId(addressId,userId);
        userAddressTxService.delete(addressId);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 修改用户地址信息
     */
    @Override
    @Transactional
    public Result<Void> update(UserAddressDTO userAddressDTO,Long addressId,Long userId) {

        if(addressId == null)
            return Result.error(Message.PARAM_ERROR);

        if(userAddressDTO.getIsDefault() == 1){
            Long defaultAddressId = userAddressTxService.getDefaultAddressId(userId);
            userAddressTxService.cancelDefaultAndUpdateUserAddressId(defaultAddressId,userId);
        }

        userAddressTxService.update(userAddressDTO,addressId);

        return Result.success(Message.OPERATION_SUCCESS);
    }



}
