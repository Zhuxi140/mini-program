package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.VO.Article.AdminArticleVO;
import com.zhuxi.service.business.ArticleService;
import com.zhuxi.service.Tx.ArticleTxService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleTxService articleTxService;

    public ArticleServiceImpl(ArticleTxService articleTxService) {
        this.articleTxService = articleTxService;
    }

    /**
     * 获取文章详情
     */
    @Override
    public Result<ArticleDetailVO> getArticleDetailById(Long id) {

        if(id == null)
            return Result.error(MessageReturn.ARTICLE_IS_NOT_EXIST);

        ArticleDetailVO articleDetailById = articleTxService.getArticleDetailById(id);
        return Result.success(MessageReturn.OPERATION_SUCCESS,articleDetailById);
    }

    /**
     * 获取文章列表
     */
    @Override
    public Result<PageResult<AdminArticleVO, Long>> getListArticleByAdmin(Long lastId, Integer pageSize, Integer type) {

        boolean first = (lastId == null || lastId < 0);
        boolean hasMore = false;

        if ( first)
            lastId = Long.MAX_VALUE;

        List<AdminArticleVO> listArticle = articleTxService.getListArticleByAdmin(lastId,pageSize + 1,type);
        boolean hasPrevious = !first;
        if(listArticle.size() == pageSize + 1){
            hasMore = true;
            lastId = listArticle.get(pageSize).getId();
            listArticle = listArticle.subList(0,pageSize);
        }
        PageResult<AdminArticleVO, Long> adminArticle = new PageResult<>(listArticle, lastId, hasPrevious, hasMore);
        return Result.success(MessageReturn.OPERATION_SUCCESS,adminArticle);
    }


    /**
     * 获取文章列表(分页+倒序)
     */
    @Override
    public Result<PageResult<ArticleVO, Long>> getListArticleDESC(Long lastId, Integer pageSize, Integer type) {
        boolean first = (lastId == null || lastId < 0);
        boolean hasMore = false;

        if ( first)
            lastId = Long.MAX_VALUE;

        List<ArticleVO> listArticleDESC = articleTxService.getListArticleDESC(lastId,pageSize+1,type);

        boolean hasPrevious = !first;
        if(listArticleDESC.size() == pageSize + 1){
            hasMore = true;
            lastId = listArticleDESC.get(pageSize).getId();
            listArticleDESC = listArticleDESC.subList(0,pageSize);
        }

        PageResult<ArticleVO, Long> pageResult = new PageResult<>(listArticleDESC, lastId, hasPrevious, hasMore);
        return Result.success(MessageReturn.OPERATION_SUCCESS,pageResult);
    }

    /**
     * 添加文章
     */
    @Override
    @Transactional
    public Result<Long> insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO) {

        if(articleInsertOrUpdateDTO == null)
            return Result.error(MessageReturn.NO_DATA);

        if((articleInsertOrUpdateDTO.getTitle() == null || articleInsertOrUpdateDTO.getTitle().isEmpty())
            || (articleInsertOrUpdateDTO.getType() == null))
            return Result.error(MessageReturn.PARAM_ERROR);

        articleTxService.insertArticle(articleInsertOrUpdateDTO);
        Long articleId = articleInsertOrUpdateDTO.getId();
        return Result.success(MessageReturn.OPERATION_SUCCESS,articleId);
    }

    /**
     * 修改文章
     */
    @Override
    @Transactional
    public Result<Void> updateArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO, Long id) {

        if (articleInsertOrUpdateDTO == null)
            return Result.error(MessageReturn.NO_DATA);

        if (id == null)
            return Result.error(MessageReturn.ARTICLE_ID_IS_NULL);

        if ((articleInsertOrUpdateDTO.getTitle() == null || articleInsertOrUpdateDTO.getTitle().isEmpty())
                && (articleInsertOrUpdateDTO.getType() == null)
                && (articleInsertOrUpdateDTO.getStatus() == null)
        ){
            return Result.error(MessageReturn.AT_LEAST_ONE_FIELD);
        }

        articleTxService.isExist(id);

        articleTxService.updateArticle(articleInsertOrUpdateDTO,id);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


    /**
     * 删除文章
     */
    @Override
    @Transactional
    public Result<Void> deleteArticle(Long id) {

        if (id == null)
            return Result.error(MessageReturn.ARTICLE_ID_IS_NULL);
        articleTxService.isExist(id);

        articleTxService.deleteArticle(id);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

}
