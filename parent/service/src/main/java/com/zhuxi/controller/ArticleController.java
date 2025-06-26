package com.zhuxi.controller;

import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.List;

@RestController
@RequestMapping("/article")
@Tag(name = "用户端接口")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @GetMapping("/{id}")
    @RequireRole(Role.USER)
    @Operation(summary = "获取文章详情")
    public Result<ArticleDetailVO>  getArticleDetail(
            @Parameter(description = "文章id",required = true)
            @PathVariable Long id
    ){
        return articleService.getArticleDetailById(id);
    }

    @GetMapping
    @RequireRole(Role.USER)
    @Operation(summary = "获取文章列表")
    public Result<List<ArticleVO>>  getArticleList(){
        return articleService.getListArticle();
    }
}
