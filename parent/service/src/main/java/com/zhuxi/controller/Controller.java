package com.zhuxi.controller;

import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.business.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.util.List;

@RestController
@Tag(name = "通用文章接口")
@RequestMapping("/Common")
public class Controller {

    private final ArticleService articleService;

    public Controller(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    @Operation(summary = "获取文章列表 ")
    public Result<List<ArticleVO>> getArticleList(
            @Parameter(description = "文章类型(默认所有类型 1为公告 2为新闻/文章)",required = false)
            @RequestParam(required = false) Integer type
    ){
        return articleService.getListArticle(type);
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

}
