package com.zhuxi.mapper;


import com.zhuxi.pojo.VO.Order.OrderShowVO;
import org.apache.ibatis.annotations.*;
import com.zhuxi.pojo.DTO.User.UserLoginDTO;
import com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import com.zhuxi.pojo.VO.Admin.AdminUserVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    // 根据id获取用户信息
    @Select("""
        SELECT id,display_name,avatar,phone,status,order_count,last_order_time
        FROM user_summary WHERE id = #{id}
        """)
    AdminUserVO getUserById(Long id);


    // 分页查询获取所有用户信息
    List<AdminUserVO> getListUserDESC(@Param("lastId") Long lastId, @Param("pageSize") Integer pageSize);

    List<AdminUserVO> getListUserASC(@Param("lastId") Long lastId, @Param("pageSize") Integer pageSize);


    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateUserStatus(Integer status, Long id);

    // 更新用户信息
    int updateUser(UserUpdateDTO userUpdateDTO);

    //登录(测试)
    @Select("SELECT id,openid,role FROM user WHERE openid = #{openid} ")
    UserLoginDTO isUserExist(String openid);

    //用户是否存在
    @Select("SELECT COUNT(*) > 0 FROM user WHERE id = #{id} ")
    boolean isUserExistById(Long id);


    @Select("SELECT id FROM us" +
            "er WHERE id > #{lastId} ORDER BY id LIMIT #{pageSize}")
    List<Long> getAllUserId(Long lastId,int pageSize);


    @Select("""
    SELECT
            o.id,
            o.order_sn,
            o.group_id,
            p.name AS product_name,
            sp.spec AS spec_name,
            p.cover_url,
            o.total_amount,
            o.status,
            o.created_at
    FROM `order` o JOIN product p ON o.product_id = p.id
    JOIN spec sp ON o.spec_id = sp.id
    WHERE o.user_id = #{userId} AND o.id < #{lastId} AND o.created_at > DATE_SUB(NOW(),INTERVAL #{days} DAY)
    ORDER BY o.id DESC
    LIMIT #{pageSize}
    """)
    List<OrderShowVO> getListOrderByDays(@Param("userId") Long userId, @Param("lastId") Long lastId, @Param("pageSize") Integer pageSize, @Param("days") Integer days);

    @MapKey("month")
    List<Map<String, Integer>> getUserTrend(@Param("Year") Integer targetYear);
}
