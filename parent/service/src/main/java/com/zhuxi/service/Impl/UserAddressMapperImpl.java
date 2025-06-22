package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.UserAddressMapper;
import com.zhuxi.service.UserAddressService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserAddressDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserAddressVO;

import java.util.List;


@Service
public class UserAddressMapperImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;
    private final JwtUtils jwtUtils;

    public UserAddressMapperImpl(UserAddressMapper userAddressMapper, JwtUtils jwtUtils) {
        this.userAddressMapper = userAddressMapper;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 添加用户地址
     */
    @Override
    @Transactional
    public Result<Void> add(UserAddressDTO userAddressDTO, String token) {

        Result<Long> result = getUserId(token);
        if(result.getCode() != 200)
            return Result.error(result.getMsg());

        Long userId = result.getData();
        int exist = userAddressMapper.isExist(userId);

        if(exist == 0){
            userAddressDTO.setIsDefault(1);
        }
        else if(exist == 5)
            return Result.error(Message.USER_ADDRESS_MAX);

        if(userAddressDTO.getIsDefault() == 1){

            Long defaultAddressId = userAddressMapper.getDefaultAddressId(userId);

            if(defaultAddressId != null)
                if(!userAddressMapper.cancelDefault(defaultAddressId))
                    return Result.error(Message.OPERATION_ERROR);
        }


        if(userAddressMapper.insert(userAddressDTO,userId)){
            if(userAddressMapper.updateUserAddressId(userAddressDTO.getId(),userId))
                return Result.success(Message.OPERATION_SUCCESS);

            return Result.success(Message.OPERATION_ERROR);
        }


        return Result.error(Message.OPERATION_ERROR);

    }

    /**
     * 设置用户地址为默认
     */
    @Override
    @Transactional
    public Result<Void> setDefault(Long addressId, String token) {

        if (addressId == null)
            return Result.error(Message.PARAM_ERROR);

        Result<Long> result = getUserId(token);

        if(result.getCode() != 200)
            return Result.error(result.getMsg());

        Long userId = result.getData();

        Long defaultAddressId = userAddressMapper.getDefaultAddressId(userId);

        if(defaultAddressId != null)
            if(!userAddressMapper.cancelDefault(defaultAddressId))
                return Result.error(Message.OPERATION_ERROR);



        if(userAddressMapper.setDefault(1,addressId)){
            if (userAddressMapper.updateUserAddressId(addressId,userId))
                return Result.success(Message.OPERATION_SUCCESS);
            return Result.success(Message.OPERATION_ERROR);
        }


        return Result.error(Message.OPERATION_ERROR);
    }


    /**
     * 取消默认地址
     */
    @Override
    @Transactional
    public Result<Void> cancelDefault(Long addressId,String token) {

        if(addressId == null)
            return Result.error(Message.PARAM_ERROR);

        Result<Long> result = getUserId(token);

        if(result.getCode() != 200)
            return Result.error(result.getMsg());

        Long userId = result.getData();

        if(userAddressMapper.cancelDefault(addressId)){
            if (userAddressMapper.updateUserAddressId(0L,userId))
                return Result.success(Message.OPERATION_SUCCESS);

            return Result.success(Message.OPERATION_ERROR);
        }

        return Result.error(Message.OPERATION_ERROR);
    }


    /**
     * 获取用户地址列表
     */
    @Override
    public Result<List<UserAddressVO>> getList(String token) {

        Result<Long> result = getUserId(token);

        if(result.getCode() != 200)
            return Result.error(result.getMsg());

        Long data = result.getData();

        List<UserAddressVO> list = userAddressMapper.getList(data);
        if(list != null)
            return Result.success(Message.OPERATION_SUCCESS,list);

        return Result.error(Message.OPERATION_ERROR);
    }

    /**
     * 删除用户地址
     */
    @Override
    @Transactional
    public Result<Void> delete(Long addressId,String token) {

        if(addressId == null)
            return Result.error(Message.PARAM_ERROR);

        Result<Long> result = getUserId(token);

        if(result.getCode() != 200)
            return Result.error(result.getMsg());

        Long userId = result.getData();

        if (userAddressMapper.isDefault(addressId))
            if(!userAddressMapper.updateUserAddressId(0L,userId))
                return Result.error(Message.OPERATION_ERROR);


        if(userAddressMapper.delete(addressId))
            return Result.success(Message.OPERATION_SUCCESS);

        return Result.error(Message.OPERATION_ERROR);
    }

    /**
     * 修改用户地址信息
     */
    @Override
    @Transactional
    public Result<Void> update(UserAddressDTO userAddressDTO,Long addressId,String token) {

        if(addressId == null)
            return Result.error(Message.PARAM_ERROR);

        Result<Long> result = getUserId(token);
        if(result.getCode() != 200)
            return Result.error(result.getMsg());

        Long userId = result.getData();

        if(userAddressDTO.getIsDefault() == 1){
            Long defaultAddressId = userAddressMapper.getDefaultAddressId(userId);
            if(defaultAddressId != null)
                if(!userAddressMapper.cancelDefault(defaultAddressId))
                    return Result.error(Message.OPERATION_ERROR);

            if(!userAddressMapper.updateUserAddressId(0L,userId))
                return Result.error(Message.OPERATION_ERROR);
        }

        if(userAddressMapper.update(userAddressDTO,addressId))
            return Result.success(Message.OPERATION_SUCCESS);

        return Result.error(Message.OPERATION_ERROR);
    }


    private Result<Long> getUserId(String token){
        if (token == null) {
            return Result.error(Message.JWT_IS_NULL);
        }

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null) {
            return Result.error(Message.JWT_ERROR);
        }

        Long userId = claims.get("id", Long.class);
        if (userId == null) {
            return Result.error(Message.JWT_DATA_ERROR);
        }

        return Result.success(userId);
    }

}
