package com.zhuxi.controller.Admin;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.CurrentAdminId;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageSync;
import com.zhuxi.pojo.entity.Role;
import com.zhuxi.service.business.DeadMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/deadMessage")
@Tag(name = "管理端接口")
public class DeadMessageController {
    private  final DeadMessageService deadMessageService;

    public DeadMessageController(DeadMessageService deadMessageService) {
        this.deadMessageService = deadMessageService;
    }

    @GetMapping("/getList")
    @RequireRole(Role.SUPER_ADMIN)
    @Operation(summary = "获取死信队列消息列表")
    public Result<PageResult<DeadMessageAddDTO, Long>> getListDeadMessages(
            @Parameter(description = "上一页游标(第一次请求不用给)")
            Long lastId,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "每页数量,后端默认为10")
            Integer pageSize
    )
    {
        return deadMessageService.getListDeadMessages(lastId, pageSize);
    }


    @PostMapping("/RetryOrRepair")
    @RequireRole(Role.SUPER_ADMIN)
    @Operation(summary = "重新发送队列消息 或  修改并重新发送队列消息")
    public Result<Void> retryDeadMessages(
            @Parameter(required = true)
            @RequestBody DeadMessageAddDTO deadMessageAddDTO,
            @Parameter(hidden = true)
            @CurrentAdminId
            Map<String,Object> adminData
    )
        {
        return deadMessageService.retryDeadMessages(deadMessageAddDTO,adminData);
    }


    @GetMapping("/sync")
    @RequireRole(Role.SUPER_ADMIN)
    @Operation(summary = "局部刷新获取最新信息")
    public Result<DeadMessageSync> syncDeadMessages(@Parameter(description = "消息ID",required = true) String messageId)
    {
        return deadMessageService.syncDeadMessages(messageId);
    }
}
