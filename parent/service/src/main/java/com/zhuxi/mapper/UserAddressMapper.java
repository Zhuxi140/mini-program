package com.zhuxi.mapper;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.User.UserAddressDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserAddressVO;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    @Insert("""
        INSERT INTO user_address(user_id,recipient,phone,province,city,district,detail,is_default)
        VALUE(#{userId},#{uADto.recipient},#{uADto.phone},#{uADto.province},#{uADto.city},#{uADto.district},#{uADto.detail},#{uADto.isDefault})
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "uADto.id")
    Boolean insert(UserAddressDTO uADto, Long userId);

    @Select("SELECT id FROM user_address WHERE is_default = 1 AND user_id = #{userId} ")
    Long getDefaultAddressId(Long userId);

    @Select("SELECT COUNT(*) FROM user_address WHERE user_id = #{userId}")
    int isExist(Long userId);

    @Update("UPDATE user_address SET is_default = 0 WHERE id = #{addressId}")
    Boolean cancelDefault(Long addressId);

    @Update("UPDATE user_address SET is_default = #{isDefault} WHERE id = #{addressId}")
    Boolean setDefault(Integer isDefault, Long addressId);

    List<UserAddressVO> getList(Long userId);

    @Delete("DELETE FROM user_address WHERE id = #{addressId}")
    Boolean delete(Long addressId);


    Boolean update( UserAddressDTO uADto,Long addressId);

    @Update("UPDATE user SET address_id = #{addressId} WHERE id = #{userId}")
    Boolean updateUserAddressId(Long addressId, Long userId);

    @Select("SELECT is_default = 1 FROM user_address WHERE  id=#{addressId}")
    Boolean isDefault(Long addressId);



}
