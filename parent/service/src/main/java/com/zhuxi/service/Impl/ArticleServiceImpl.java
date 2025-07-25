package com.zhuxi.service.Impl;

import com.zhuxi.Constant.Message;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.service.ArticleService;
import com.zhuxi.service.TxService.ArticleTxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
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
    public Result<List<ArticleVO>> getListArticle(Integer type) {

        List<ArticleVO> listArticle = articleTxService.getListArticle(type);
        return Result.success(Message.OPERATION_SUCCESS,listArticle);
    }

    /**
     * 获取文章列表(分页+倒序)
     */
    @Override
    public PageResult<List<ArticleVO>,Long> getListArticleDESC(Long lastId, Integer pageSize, Integer type) {
        boolean first = (lastId == null || lastId < 0);
        boolean hasMore = false;

        if ( first)
            lastId = 0L;

        List<ArticleVO> listArticleDESC = articleTxService.getListArticleDESC(lastId,pageSize+1,type);

        boolean hasPrevious = !first;
        if(listArticleDESC.size() > pageSize){
            hasMore = true;
            listArticleDESC = listArticleDESC.subList(0,pageSize);
        }

        if (!listArticleDESC.isEmpty())
             lastId = listArticleDESC.get(listArticleDESC.size() - 1).getId();

        return new PageResult(listArticleDESC,lastId,hasPrevious,hasMore);
    }

    /**
     * 添加文章
     */
    @Override
    @Transactional
    public Result<Void> insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO) {

        if(articleInsertOrUpdateDTO == null)
            return Result.error(Message.NO_DATA);

        if((articleInsertOrUpdateDTO.getTitle() == null || articleInsertOrUpdateDTO.getTitle().isEmpty())
            || (articleInsertOrUpdateDTO.getType() == null))
            return Result.error(Message.PARAM_ERROR);

        articleTxService.insertArticle(articleInsertOrUpdateDTO);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    /**
     * 修改文章
     */
    @Override
    @Transactional
    public Result<Void> updateArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO, Long id) {

        if (articleInsertOrUpdateDTO == null)
            return Result.error(Message.NO_DATA);

        if (id == null)
            return Result.error(Message.ARTICLE_ID_IS_NULL);

        if ((articleInsertOrUpdateDTO.getTitle() == null || articleInsertOrUpdateDTO.getTitle().isEmpty())
                && (articleInsertOrUpdateDTO.getType() == null)
                && (articleInsertOrUpdateDTO.getStatus() == null)
        )
            return Result.error(Message.AT_LEAST_ONE_FIELD);

        articleTxService.isExist(id);

        articleTxService.updateArticle(articleInsertOrUpdateDTO,id);

        return Result.success(Message.OPERATION_SUCCESS);
    }


    /**
     * 删除文章
     */
    @Override
    @Transactional
    public Result<Void> deleteArticle(Long id) {

        if (id == null)
            return Result.error(Message.ARTICLE_ID_IS_NULL);
        articleTxService.isExist(id);

        articleTxService.deleteArticle(id);

        return Result.success(Message.OPERATION_SUCCESS);
    }

}
