package com.zhuxi.mapper;


import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.Admin.AdminUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {


    //根据用户名查询用户信息
    @Select("SELECT id,password,username,role FROM admin WHERE username = #{username} ")
    AdminLoginDTO getPasswordByUsername(String username);


    // 判断用户名是否存在
    @Select("SELECT COUNT(*) > 0 FROM admin WHERE username = #{username}")
    boolean isExists(String username);

    // 更新用户最后登录时间
    @Update("UPDATE admin SET last_login = #{lastLogin} WHERE id = #{id}")
    int updateLastLogin(Integer id, LocalDateTime lastLogin);

    // 根据id查询用户信息
    @Select("""
            SELECT id,username,real_name,status,role,last_login
            FROM admin WHERE id = #{id}
        """)
    AdminVO getAdminById(Integer id);


    // 添加用户
    @Insert(
    """
    INSERT INTO admin(username, password, real_name, status)
    VALUES(#{username}, #{password}, #{realName}, #{status})
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Boolean insertAdmin(Admin  admin);

    // 修改用户信息
    int updateAdmin(AdminUpdateDTO admin);

    // 查询所有用户信息
    List<AdminVO> queryAdminList();

    //删除用户
    @Delete("DELETE FROM admin WHERE id = #{id}")
    Boolean deleteAdmin(Integer id);
}
