package com.zhuxi.controller.User;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductSpecVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "用户端接口")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取商品列表接口",
            description = "商品展示"
    )
    public Result<PageResult<ProductOverviewVO>> getListProducts(
            @Parameter(description = "商品id", required = true)
            Long lastId,
            @Parameter(description = "分页大小(后端默认为10)", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize){
        return productService.getListProducts(lastId, pageSize);
    }

    @GetMapping("/{id}")
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取商品详情接口",
            description = "商品详情"
    )
    public Result<ProductDetailVO> getProductDetail(
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id){
        return productService.getProductDetail(id);
    }

    @GetMapping("/spec/{id}")
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取商品规格接口",
            description = "商品规格"
    )
    public Result<List<ProductSpecVO>> getProductSpec(
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id){
        return productService.getProductSpec(id);
    }
}
