package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import src.main.java.com.zhuxi.pojo.DTO.article.ArticleOssUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Service
public class ArticleTxService {

    private final ArticleMapper articleMapper;

    public ArticleTxService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Transactional(readOnly = true)
    public Boolean isExist(Long id) {
        if(articleMapper.isExist(id) <= 0)
            throw new transactionalException(Message.ARTICLE_IS_NOT_EXIST);

    }

    @Transactional(readOnly = true)
    public ArticleDetailVO getArticleDetailById(Long id) {
        ArticleDetailVO articleDetailById = articleMapper.getArticleDetailById(id);
        if(articleDetailById == null)
            throw new RuntimeException(Message.ARTICLE_IS_NOT_EXIST);
        return articleDetailById;
    }

    @Transactional(readOnly = true)
    public List<ArticleVO> getListArticle(Integer  type) {
        List<ArticleVO> listArticle = articleMapper.getListArticle(type);
        if(listArticle.isEmpty())
            throw new transactionalException(Message.NO_DATA);
        return listArticle;
    }

    @Transactional(readOnly = true)
    public List<ArticleVO> getListArticleDESC(Long lastId, Integer pageSize,Integer type) {
        List<ArticleVO> listArticleDESC = articleMapper.getListArticleDESC(lastId, pageSize,type);
        if(listArticleDESC.isEmpty())
            throw new transactionalException(Message.NO_DATA);
        return listArticleDESC;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO) {
        Boolean b = articleMapper.insertArticle(articleInsertOrUpdateDTO);
        if(!b)
            throw new transactionalException(Message.INSERT_ERROR);
    }


    @Transactional(rollbackFor = transactionalException.class)
    public void updateArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO,Long id) {
        int i = articleMapper.updateArticle(articleInsertOrUpdateDTO, id);
        if(i <= 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void deleteArticle(Long id) {
        Boolean b = articleMapper.deleteArticle(id);
        if(!b)
            throw new transactionalException(Message.DELETE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateArticleOss(ArticleOssUpdateDTO articleOssUpdateDTO) {
        int i = articleMapper.updateArticleOss(articleOssUpdateDTO);
        if(i <= 0)
            throw new transactionalException(Message.UPDATE_ERROR);
    }
}
