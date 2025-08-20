package com.zhuxi.controller.Admin;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.pojo.VO.Article.AdminArticleVO;
import com.zhuxi.service.business.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("adminArticle")
@Tag(name = "管理端接口")
public class AdminArticleController {
    private final ArticleService adminArticleService;

    public AdminArticleController(ArticleService adminArticleService) {
        this.adminArticleService = adminArticleService;
    }

    @PostMapping
    @RequireRole(Role.ADMIN)
    @Operation(summary = "添加文章接口")
    public Result<Void> addArticle(@RequestBody ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO) {
        return adminArticleService.insertArticle(articleInsertOrUpdateDTO);
    }

    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(summary = "修改文章接口(修改基础参数)")
    public Result<Void> modifyArticle(
            @RequestBody ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO,
            @Parameter(description = "文章id",required = true)
            @PathVariable Long id

    ) {
        return adminArticleService.updateArticle(articleInsertOrUpdateDTO,id);
    }


    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    @Operation(summary = "删除文章接口")
    public Result<Void> deleteArticle(
            @Parameter(description = "文章id",required = true)
            @PathVariable Long id
    ) {
        return adminArticleService.deleteArticle(id);
    }

    @GetMapping("/list")
    @RequireRole(Role.ADMIN)
    @Operation(summary = "获取文章列表接口")
    public Result<PageResult<AdminArticleVO, Long>> getListArticleDESC(
            @Parameter(description = "上一条数据的id")
            Long lastId,
            @Parameter(description = "每页数据量")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "文章类型")
            Integer type
    ) {
        return adminArticleService.getListArticleByAdmin(lastId,pageSize,type);
    }
}
