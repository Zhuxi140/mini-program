package com.zhuxi.service.business;

import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import com.zhuxi.pojo.VO.Article.AdminArticleVO;
import com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import com.zhuxi.pojo.VO.Article.ArticleVO;

public interface ArticleService {

     //获取文章详情
    Result<ArticleDetailVO> getArticleDetailById(Long id);

    //获取文章列表分页倒序
    Result<PageResult<ArticleVO, Long>> getListArticleDESC(Long lastId, Integer pageSize, Integer  type);

    //添加文章
    Result<Long> insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO);

    //修改文章
    Result<Void> updateArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO,Long id);

    //删除文章
    Result<Void> deleteArticle(Long id);

    // 获取文章列表
    Result<PageResult<AdminArticleVO, Long>> getListArticleByAdmin(Long lastId, Integer pageSize, Integer type);
}
