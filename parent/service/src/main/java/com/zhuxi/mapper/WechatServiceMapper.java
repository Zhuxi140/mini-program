package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;
import com.zhuxi.pojo.VO.User.UserBasicVO;

import java.util.List;

@Mapper
public interface WechatServiceMapper {

    @Select("SELECT COUNT(*) FROM user WHERE openid = #{openId}")
    int isExist(String openId);

    @Select("SELECT status FROM user WHERE openid = #{openId}")
    int isBan(String openId);

    @Insert("""
    INSERT user(snowflake_id, openid) VALUE (#{snowflakeId},#{openId})
    """)
    int insert(Long snowflakeId, String openId);

    @Insert("Update user SET name = #{name},avatar=#{avatar} WHERE id = #{id} ")
    int insertUser(UserBasicDTO userBasicDTO);

    @Select("""
    SELECT id,
           name,
           avatar,
           openid
    FROM user
    WHERE id > #{lastId}
    ORDER BY id
    LIMIT #{pageSize}
    """)
    List<UserBasicDTO> getUserInfo(Long lastId, int pageSize);

    @Select("SELECT name,avatar FROM user WHERE openid = #{openid}")
    UserBasicVO getUserBasicInfo(String openid);


    @Select("SELECT id FROM user WHERE openid = #{openid}")
    Long getUserId(String openid);

    @Update("UPDATE user SET phone = #{phone} WHERE id = #{userId}")
    int InsertPhone(String phone, Long userId);

}
