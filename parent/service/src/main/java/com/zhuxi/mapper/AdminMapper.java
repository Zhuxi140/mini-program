package com.zhuxi.mapper;


import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.AdminLoginDTO;
import src.main.java.com.zhuxi.pojo.VO.AdminVO;
import src.main.java.com.zhuxi.pojo.entity.Admin;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("SELECT id,password,username FROM admin WHERE username = #{username} ")
    AdminLoginDTO getPasswordByUsername(String username);

    @Select("SELECT COUNT(*) > 0 FROM admin WHERE username = #{username}")
    boolean isExists(String username);

    @Update("UPDATE admin SET last_login = #{lastLogin} WHERE id = #{id}")
    int updateLastLogin(Integer id, LocalDateTime lastLogin);

    @Select("""
            SELECT id,username,real_name,status,last_login
            FROM admin WHERE id = #{id}
        """)
    AdminVO getAdminById(Integer id);


    @Insert(
    """
    INSERT INTO admin(username, password, real_name, status)
    VALUES(#{username}, #{password}, #{realName}, #{status})
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Boolean insertAdmin(Admin  admin);

    int updateAdmin(AdminVO  admin);

    List<AdminVO> queryAdminList();
}
