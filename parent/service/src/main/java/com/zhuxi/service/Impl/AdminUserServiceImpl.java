package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.VO.Order.OrderShowVO;
import com.zhuxi.service.business.AdminUserService;
import com.zhuxi.service.Tx.AdminUserTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zhuxi.pojo.VO.Admin.AdminUserVO;

import java.util.List;
import java.util.Map;

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
            return Result.error(MessageReturn.PARAM_ERROR);

        PageResult<AdminUserVO,Long> adminUserVO;
        if(DESC.equals(1))
            adminUserVO = PageDesc(lastId, pageSize);
        else
            adminUserVO = PageAsc(lastId, pageSize);


        return Result.success(MessageReturn.OPERATION_SUCCESS, adminUserVO);
    }


    /**
     * 禁用用户
     */
    @Override
    @Transactional
    public Result<Void> disableUser(Integer status,Long id) {

        if(status == null || status < 0 || status > 1)
            return Result.error(MessageReturn.PARAM_ERROR);
        if(id == null || id <= 0)
            return Result.error(MessageReturn.PARAM_ERROR);

        adminUserTxService.isUserExist( id);

        adminUserTxService.updateUserStatus(status, id);
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    /**
     * 获取用户订单历史列表（近dys天）
     */
    @Override
    public Result<PageResult> getUserOrder(Long userId,Long lastId, Integer pageSize, Integer days) {

        boolean hasMore = false;
        boolean first = (lastId == null || lastId <= 0);
        boolean previous = !first;
        if ( first){
            lastId = Long.MAX_VALUE;
        }

        List<OrderShowVO> result = adminUserTxService.getListOrderByDays(userId, lastId, pageSize + 1, days);
        if (result.size() == pageSize + 1){
            hasMore = true;
            lastId = result.get(pageSize).getId();
            result = result.subList(0, pageSize);
        }

        PageResult<OrderShowVO,Long> pageResult = new PageResult<>(result, lastId, previous, hasMore);
        return Result.success(MessageReturn.OPERATION_SUCCESS, pageResult);
    }

    /**
     * 获取用户趋势
     */
    @Override
    public Result<List<Map<String, Integer>>> getUserTrend(Integer targetYear) {
        if (targetYear != null && targetYear.toString().length() != 4){
            return Result.error(MessageReturn.YEAR_IS_NOT_4_DIGIT);
        }

        List<Map<String, Integer>> result = adminUserTxService.getUserTrend(targetYear);
        return Result.success(MessageReturn.OPERATION_SUCCESS, result);
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
