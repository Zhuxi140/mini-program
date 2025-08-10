package com.zhuxi.service.business;

import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.User.UserAddressDTO;
import com.zhuxi.pojo.VO.User.UserAddressVO;

import java.util.List;

public interface UserAddressService {

    Result<Void> add(UserAddressDTO userAddressDTO, Long userId);

    Result<Void> setDefault(Long addressId , Long userId);

    Result<Void> cancelDefault(Long addressId,Long userId);

    Result<List<UserAddressVO>> getList(Long userId);

    Result<Void> delete(Long addressId,Long userId);

    Result<Void> update(UserAddressDTO userAddressDTO, Long addressId,Long userId);
}
