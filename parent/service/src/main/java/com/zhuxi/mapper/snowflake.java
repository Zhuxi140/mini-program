package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface snowflake {

    @Select("SELECT snowflake_id FROM spec WHERE id = #{id}")
    Integer getUserId(Long id);

    @Update("UPDATE spec SET snowflake_id = #{snowflakeId} WHERE id = #{id}")
    int updateUserId(Long id,Long snowflakeId);
}
