package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.ArticleService;
import com.zhuxi.service.TxService.ArticleTxService;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private ArticleTxService articleTxService;

    public ArticleServiceImpl(ArticleTxService articleTxService) {
        this.articleTxService = articleTxService;
    }

    /**
     * 获取文章详情
     */
    @Override
    public Result<ArticleDetailVO> getArticleDetailById(Long id) {

        if(id == null)
            return Result.error(Message.ARTICLE_IS_NOT_EXIST);

        ArticleDetailVO articleDetailById = articleTxService.getArticleDetailById(id);
        return Result.success(Message.OPERATION_SUCCESS,articleDetailById);
    }


    /**
     * 获取文章列表
     */
    @Override
    public Result<List<ArticleVO>> getListArticle() {

        List<ArticleVO> listArticle = articleTxService.getListArticle();
        return Result.success(Message.OPERATION_SUCCESS,listArticle);
    }

    /**
     * 获取文章列表(分页+倒序)
     */
    @Override
    public Result<List<ArticleVO>> getListArticleDESC(Long lastId, Integer pageSize) {
        if(lastId == null)
            return Result.error(Message.ARTICLE_ID_IS_NULL);

        List<ArticleVO> listArticleDESC = articleTxService.getListArticleDESC(lastId,pageSize);
        return Result.success(Message.OPERATION_SUCCESS,listArticleDESC);
    }
}
