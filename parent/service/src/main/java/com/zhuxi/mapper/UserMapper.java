package com.zhuxi.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.AdminUserVO;

import java.util.List;

@Mapper
public interface UserMapper {

    // 根据id获取用户信息
    @Select("""
        SELECT id,phone,nickname,wx_nickname,wx_avatar_url,custom_avatar_oss,status
        FROM user WHERE id = #{id}
        """)
    AdminUserVO getUserById(Integer id);


    // 分页查询获取所有用户信息
    List<AdminUserVO> getListUser(@Param("lastId") Integer lastId, @Param("pageSize") Integer pageSize);

    // 更新用户信息
    int updateUser(UserUpdateDTO userUpdateDTO);

    //登录(测试)
    @Select("SELECT id,openid,role FROM user WHERE openid = #{openid} ")
    UserLoginDTO isUserExist(String openid);

}
