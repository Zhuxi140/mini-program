package com.zhuxi.controller.User;


import com.zhuxi.Result.ProductPageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.BloomFilterCheck;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.ProductService;
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
    public Result<ProductPageResult<ProductOverviewVO>> getListProducts(
            @Parameter(description = "下一页的游标(第一次不用给，后续根据json给值)", required = true)
            Long lastScore,
            @Parameter(description = "分页大小(后端默认为10)", hidden = true)
                @RequestParam(defaultValue = "10")
            Integer pageSize,
            @Parameter(description = "商品类型(1:默认序列，2:按价格低到高排序  3:按价格高到低排序)  默认为1", required = true)
                @RequestParam(defaultValue = "1")
            Integer type,
            @Parameter(description = "是否redis已经查询完了(当redis中未命中(hasNext=false)，则其后一直给true)")
                @RequestParam(defaultValue = "false")
            Boolean isLast
    )
    {
        return productService.getListProducts(lastScore, pageSize,type,isLast);
    }

    @GetMapping("/detail")
    @BloomFilterCheck(BloomFilterName = "product",key1 = "ProductNumber")
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取商品详情接口",
            description = "商品详情"
    )
    public Result<ProductDetailVO> getProductDetail(
            @Parameter(description = "商品号", required = true)
            Long ProductNumber){
        return productService.getProductDetail(ProductNumber);
    }

    @GetMapping("/spec")
    @BloomFilterCheck(BloomFilterName = "product",key1 = "ProductNumber")
    @RequireRole(Role.USER)
    @Operation(
            summary = "获取商品规格接口",
            description = "商品规格"
    )
    public Result<List<ProductSpecVO>> getProductSpec(
            @Parameter(description = "商品号", required = true)
            Long ProductNumber){
        return productService.getProductSpec(ProductNumber);
    }
}
