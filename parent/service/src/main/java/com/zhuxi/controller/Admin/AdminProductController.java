package com.zhuxi.controller.Admin;


import com.fasterxml.jackson.annotation.JsonView;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.List;

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
    public Result<PageResult> getListProducts(
            @Parameter(description = "商品id(第一次请求无需给值)", required = true)
            Long lastId,
            @Parameter(description = "分页大小(后端默认为10)", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "排序方式, 为1时为降序",required = true)
            Integer DESC
    )
    {
        return adminProductService.getListAdminProducts(lastId, pageSize,DESC);
    }


    @GetMapping("/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "获取商品规格详细详情",
            description = "用于管理端在点击规格详细后展示商品规格及库存 并做库存预警处理"
    )
    public Result<List<ProductSpecDetailVO>> getProductSpecDetail(
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id
    )
    {
        return adminProductService.getProductSpecDetail(id);
    }


    @PostMapping
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "添加商品",
            description = "添加商品时，为确保数据统一，可售库存强制为0"
    )
    public Result<Void> add(
            @Parameter(description = "商品信息", required = true)
            @RequestBody
            ProductAddDTO productAddDTO
    ){
        return adminProductService.add(productAddDTO);
    }


    @PutMapping
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "修改商品",
            description = "至少修改一个字段"
    )
    public Result<Void> update(
            @Parameter(description = "商品信息", required = true)
            @RequestBody
            ProductUpdateDTO productUpdateDTO
    ){
        return adminProductService.update(productUpdateDTO);
    }


    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "删除商品",
            description = "删除商品"
    )
    public Result<Void> delete(
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id
    ){
        return adminProductService.delete(id);
    }

    @PutMapping("/putOnSale/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "商品上架",
            description = "商品上架"
    )
    public Result<Void> putOnSale(
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id
    ){
        return adminProductService.putOnSale(id);
    }
}
