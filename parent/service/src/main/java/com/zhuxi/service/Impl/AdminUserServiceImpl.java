package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.UserMapper;
import com.zhuxi.service.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminUserVO;

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
    public Result<PageResult<AdminUserVO>> getListUser(Integer lastId, Integer pageSize,Integer DESC) {

        if(DESC == null)
            return Result.error(Message.PARAM_ERROR);

        PageResult<AdminUserVO> adminUserVO;
        if(DESC.equals(1))
            adminUserVO = PageDesc(lastId, pageSize);
        else
            adminUserVO = PageAsc(lastId, pageSize);


        return Result.success(Message.OPERATION_SUCCESS, adminUserVO);
    }


    /**
     * 禁用用户
     */
    @Override
    @Transactional
    public Result<Void> disableUser(Integer status,Integer id) {

        if(status == null || status < 0 || status > 1)
            return Result.error(Message.PARAM_ERROR);
        if(id == null || id <= 0)
            return Result.error(Message.PARAM_ERROR);

        int i = userMapper.updateUserStatus(status, id);
        if(i < 1)
            return Result.error(Message.OPERATION_ERROR);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 降序分页获取所有用户信息列表
     */
    private PageResult<AdminUserVO> PageDesc(Integer lastId, Integer pageSize){
        boolean hasMore = false;
        Long nextCursor = null;

        if (lastId == null || lastId <= 0) {
            lastId = Integer.MAX_VALUE;
        }

        List<AdminUserVO> items = userMapper.getListUserDESC(lastId, pageSize + 1);

        if (items.size() == pageSize + 1) {
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if (!items.isEmpty()) {
            nextCursor = items.get(items.size() - 1).getId();
        }

        boolean hasPrevious = lastId != Integer.MAX_VALUE;

        return new PageResult<>(items, nextCursor, hasPrevious, hasMore);
    }

    /**
     * 升序分页获取所有用户信息列表
     */
    private PageResult<AdminUserVO> PageAsc(Integer lastId, Integer pageSize){
        boolean hasMore = false;
        Long nextCursor = null;

        if(lastId == null)
            lastId = 0;

        List<AdminUserVO> items = userMapper.getListUserASC(lastId, pageSize + 1);

        if(items.size() == pageSize + 1){
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if(!items.isEmpty()){
            nextCursor = items.get(items.size() - 1).getId();
        }

        boolean hasPrevious = lastId != 0;

        return new PageResult<>(items, nextCursor, hasPrevious, hasMore);
    }


}
