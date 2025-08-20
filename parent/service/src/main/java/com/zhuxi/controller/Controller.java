package com.zhuxi.controller;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.business.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import com.zhuxi.pojo.VO.Article.ArticleVO;

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
    public Result<PageResult<ArticleVO, Long>> getArticleList(
            @Parameter(description = "文章类型(默认所有类型 1为公告 2为新闻/文章)",required = false)
            Integer type,
            @Parameter(description = "下一页游标（第一次不用给）",required = false)
            Long lastId,
            @Parameter(description = "分页大小",required = false)
            @RequestParam(defaultValue = "10")
            Integer pageSize

    ){
        return articleService.getListArticleDESC(lastId,pageSize,type);
    }


    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<ArticleDetailVO>  getArticleDetail(
            @Parameter(description = "文章id",required = true)
            @PathVariable Long id
    ){
        return articleService.getArticleDetailById(id);
    }

}
