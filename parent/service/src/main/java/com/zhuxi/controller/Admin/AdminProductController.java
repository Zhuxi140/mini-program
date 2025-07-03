package com.zhuxi.controller.Admin;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.product.ProductUpdateDTO;
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

    @PostMapping
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "添加商品",
            description = "添加商品"
    )
    public Result<Void> add(
            @Parameter(description = "商品信息", required = true)
            @RequestBody
            ProductAddDTO productAddDTO
    ){
        return adminProductService.add(productAddDTO);
    }


    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "修改商品",
            description = "修改商品"
    )
    public Result<Void> update(
            @Parameter(description = "商品信息", required = true)
            @RequestBody
            ProductUpdateDTO productUpdateDTO,
            @Parameter(description = "商品id", required = true)
            @PathVariable
            Long id
    ){
        return adminProductService.update(productUpdateDTO,id);
    }
}
