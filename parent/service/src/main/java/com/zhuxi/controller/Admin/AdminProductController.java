package com.zhuxi.controller.Admin;


import com.fasterxml.jackson.annotation.JsonView;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.pojo.DTO.Admin.DashboardDTO;
import com.zhuxi.pojo.DTO.product.SpecAddDTO;
import com.zhuxi.pojo.DTO.product.newProductPurchase;
import com.zhuxi.pojo.VO.Product.SupplierVO;
import com.zhuxi.service.business.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.zhuxi.pojo.DTO.product.ProductAddDTO;
import com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
import com.zhuxi.pojo.VO.Product.ProductSpecDetailVO;
import com.zhuxi.pojo.entity.Role;

import java.util.List;
import java.util.Map;

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

    @PutMapping("/stopSale/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "商品下架",
            description = "商品下架"
    )
    public Result<Void> stopSale(
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id
    ){
        return adminProductService.stopSale(id);
    }


    @PostMapping("/purchase")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "商品采购",
            description = "商品采购"
    )
    public Result<Void> purchase(
            @Parameter(description = "商品采购信息", required = true)
            @RequestBody
            newProductPurchase newProductPurchase
    ){
        return adminProductService.purchase(newProductPurchase);
    }


    @GetMapping("/supplierList")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "获取供应商列表",
            description = "获取供应商列表"
    )
    public Result<PageResult<SupplierVO, Integer>> getSupplierList(
            @Parameter(description = "供应商id(第一次请求无需给值)", required = true)
            Integer lastId,
            @Parameter(description = "分页大小(后端默认为10)")
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        return adminProductService.getSupplierList(lastId, pageSize);
    }

    @PostMapping("/addSpec")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "添加商品规格",
            description = "添加商品规格"
    )
    public Result<Void> addSpec(
            @Parameter(description = "商品规格信息", required = true)
            @RequestBody
            List<SpecAddDTO> SpecAddDTO
    ){
        return adminProductService.addSpec(SpecAddDTO);
    }

    @GetMapping("/dashboard")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "获取仪表盘数据",
            description = "获取仪表盘数据"
    )
    public Result<DashboardDTO> getDashboardData(){
        return adminProductService.getDashboardData();
    }

    @GetMapping("/profitData")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "获取利润数据",
            description = "获取利润数据"
    )
    public Result<List<Map<String, Object>>> getProfitData(@Schema(description = "查询年份") Integer targetYear){
        return adminProductService.getProfitData(targetYear);
    }

}
