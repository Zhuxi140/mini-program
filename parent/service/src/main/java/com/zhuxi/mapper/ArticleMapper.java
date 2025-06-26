package com.zhuxi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Mapper
public interface ArticleMapper {


    @Select("""
        SELECT id,title,type,content_oss_key,content_images,created_at FROM article WHERE id = #{id} """)
    ArticleDetailVO getArticleDetailById(Long id);

    List<ArticleVO> getListArticle();

    List<ArticleVO> getListArticleDESC(Long lastId, Integer pageSize);

}
