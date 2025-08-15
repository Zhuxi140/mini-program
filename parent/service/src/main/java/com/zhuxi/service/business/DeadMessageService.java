package com.zhuxi.service.business;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageSync;

import java.util.Map;

public interface DeadMessageService {

    // 展示死信错误信息
    Result<PageResult<DeadMessageAddDTO, Long>> getListDeadMessages(Long lastId, Integer pageSize);

    // 重试死信/修改并重试
    Result<Void> retryDeadMessages(DeadMessageAddDTO deadMessageAddDTO, Map<String,Object> AdminData);

    Result<DeadMessageSync> syncDeadMessages(String messageId);
}
