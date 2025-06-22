package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.User.UserAddressDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserAddressVO;

import java.util.List;

public interface UserAddressService {

    Result<Void> add(UserAddressDTO userAddressDTO, String token);

    Result<Void> setDefault(Long addressId , String token);

    Result<Void> cancelDefault(Long addressId,String token);

    Result<List<UserAddressVO>> getList(String token);

    Result<Void> delete(Long addressId,String token);

    Result<Void> update(UserAddressDTO userAddressDTO, Long addressId,String token);
}
