package com.zhuxi.service;

import com.zhuxi.Result.Result;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

public interface ArticleService {

    Result<ArticleDetailVO> getArticleDetailById(Long id);

    Result<List<ArticleVO>> getListArticle();

    Result<List<ArticleVO>> getListArticleDESC(Long lastId, Integer pageSize);
}
