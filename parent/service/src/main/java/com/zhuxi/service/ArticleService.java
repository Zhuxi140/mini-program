package com.zhuxi.service;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

public interface ArticleService {

     //获取文章详情
    Result<ArticleDetailVO> getArticleDetailById(Long id);

    //获取文章列表
    Result<List<ArticleVO>> getListArticle(Integer  type);

    //获取文章列表分页倒序
    PageResult<List<ArticleVO>> getListArticleDESC(Long lastId, Integer pageSize, Integer  type);

    //添加文章
    Result<Void> insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO);

    //修改文章
    Result<Void> updateArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO,Long id);

    //删除文章
    Result<Void> deleteArticle(Long id);
}
