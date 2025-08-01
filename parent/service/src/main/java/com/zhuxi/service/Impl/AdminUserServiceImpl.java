package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.business.AdminUserService;
import com.zhuxi.service.Tx.AdminUserTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminUserVO;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserTxService adminUserTxService;

    public AdminUserServiceImpl(AdminUserTxService adminUserTxService) {
        this.adminUserTxService = adminUserTxService;
    }

    /**
     * 分页获取所有用户信息列表
     */
    @Override
    public Result<PageResult> getListUser(Long lastId, Integer pageSize, Integer DESC) {

        if(DESC == null)
            return Result.error(Message.PARAM_ERROR);

        PageResult<AdminUserVO,Long> adminUserVO;
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
    public Result<Void> disableUser(Integer status,Long id) {

        if(status == null || status < 0 || status > 1)
            return Result.error(Message.PARAM_ERROR);
        if(id == null || id <= 0)
            return Result.error(Message.PARAM_ERROR);

        adminUserTxService.isUserExist( id);

        adminUserTxService.updateUserStatus(status, id);
        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 降序分页获取所有用户信息列表
     */
    private PageResult<AdminUserVO,Long> PageDesc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        boolean first = (lastId == null || lastId <= 0);

        if (first)
            lastId = Long.MAX_VALUE;

        List<AdminUserVO> items = adminUserTxService.getListUserDESC(lastId, pageSize + 1);

        boolean hasPrevious = !first;
        if (items.size() == pageSize + 1) {
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if (!items.isEmpty()) {
            lastId = items.get(items.size() - 1).getId();
        }


        return new PageResult<>(items, lastId, hasPrevious, hasMore);
    }

    /**
     * 升序分页获取所有用户信息列表
     */
    private PageResult<AdminUserVO, Long> PageAsc(Long lastId, Integer pageSize){
        boolean hasMore = false;
        boolean first = (lastId == null || lastId <= 0);
        if(first)
            lastId = 0L;

        List<AdminUserVO> items = adminUserTxService.getListUserASC(lastId, pageSize + 1);

        boolean hasPrevious = !first;
        if(items.size() == pageSize + 1){
            hasMore = true;
            items = items.subList(0, pageSize);
        }

        if(!items.isEmpty()){
            lastId = items.get(items.size() - 1).getId();
        }


        return new PageResult<>(items, lastId, hasPrevious, hasMore);
    }


}
