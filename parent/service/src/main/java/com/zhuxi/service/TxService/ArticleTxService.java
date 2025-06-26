package com.zhuxi.service.TxService;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Service
public class ArticleTxService {

    private final ArticleMapper articleMapper;

    public ArticleTxService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public ArticleDetailVO getArticleDetailById(Long id) {
        ArticleDetailVO articleDetailById = articleMapper.getArticleDetailById(id);
        if(articleDetailById == null)
            throw new RuntimeException(Message.ARTICLE_IS_NOT_EXIST);
        return articleDetailById;
    }

    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<ArticleVO> getListArticle() {
        List<ArticleVO> listArticle = articleMapper.getListArticle();
        if(listArticle.isEmpty())
            throw new transactionalException(Message.NO_DATA);
        return listArticle;
    }

    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<ArticleVO> getListArticleDESC(Long lastId, Integer pageSize) {
        List<ArticleVO> listArticleDESC = articleMapper.getListArticleDESC(lastId, pageSize);
        if(listArticleDESC.isEmpty())
            throw new transactionalException(Message.NO_DATA);
        return listArticleDESC;
    }

}
