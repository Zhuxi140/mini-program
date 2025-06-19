package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.UserMapper;
import com.zhuxi.service.AdminUserService;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.AdminUserVO;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private  final UserMapper userMapper;

    public AdminUserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }




    /**
     * 分页获取所有用户信息列表
     */
    @Override
    public Result<PageResult<AdminUserVO>> getListUser(Integer lastId, Integer pageSize) {

        boolean hasMore = false;
        Long nextCursor = null;

        if(lastId == null)
            lastId = 0;

        List<AdminUserVO> items = userMapper.getListUser(lastId, pageSize);

        if(items.size() == pageSize + 1){
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if(!items.isEmpty()){
            nextCursor = items.get(items.size() - 1).getId();
        }

        boolean hasPrevious = lastId != 0;
        PageResult<AdminUserVO> adminUserVOPageResult = new PageResult<>(items, nextCursor, hasPrevious, hasMore);

        return Result.success(Message.OPERATION_SUCCESS, adminUserVOPageResult);
    }


}
