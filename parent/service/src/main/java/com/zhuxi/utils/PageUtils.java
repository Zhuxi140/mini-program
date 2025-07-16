package com.zhuxi.utils;

import com.zhuxi.Result.PageResult;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

import java.util.List;

public class PageUtils {



    // 倒叙分页
    public static <T> PageResult<T> descPageSelect(List<ProductOverviewVO> list, Long lastId, Integer pageSize,boolean first){
        boolean hasMore = false;
        boolean hasPrevious = !first;

        if(list.size() == pageSize + 1){
            hasMore = true;
            list = list.subList(0, pageSize);
        }

        if(!list.isEmpty()){
            lastId = list.get(list.size() - 1).getId();
        }

        return new PageResult(list, lastId, hasPrevious, hasMore);
    }
}
