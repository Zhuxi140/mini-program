package com.zhuxi.controller;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminProductVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/adminProducts")
@Tag(name = "管理端接口")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    @GetMapping
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "获取商品状况列表",
            description = "分页获取所有商品信息列表"
    )
    public Result<PageResult<AdminProductVO> > getListProducts(
            @Parameter(description = "商品id", required = true)
            Long lastId,
            @Parameter(description = "分页大小(后端默认为10)", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "排序方式, 为1时为降序",required = true)
            Integer DESC
    )
    {
        return adminProductService.getListAdminProducts(lastId, pageSize,DESC);
    }
}
