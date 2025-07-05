package com.zhuxi.mapper;

import org.apache.ibatis.annotations.*;
import src.main.java.com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import src.main.java.com.zhuxi.pojo.DTO.article.ArticleOssUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import src.main.java.com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Mapper
public interface ArticleMapper {


    @Select("""
        SELECT id,title,type,content_oss_key,content_images,created_at FROM article WHERE id = #{id} """)
    ArticleDetailVO getArticleDetailById(Long id);

    List<ArticleVO> getListArticle(Integer type);

    List<ArticleVO> getListArticleDESC(Long lastId, Integer pageSize, Integer type);

    @Insert("""
        INSERT INTO article(title,status, type)
        VALUE (#{title},#{status},#{type})
        """)
        Boolean insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO);


    int updateArticle(ArticleInsertOrUpdateDTO aIOUDto,Long id);

    @Delete("DELETE article FROM article WHERE id =  #{id}")
    Boolean deleteArticle(Long id);

    @Update("""
    UPDATE article SET content_oss_key = #{contentOssKey},
                   cover_oss = #{contentOss},
               content_images=#{contentImages,typeHandler=com.zhuxi.handler.TypeHandler}
    WHERE id = #{aritcleId}
    """)
    int updateArticleOss(ArticleOssUpdateDTO articleOssUpdateDTO);
}
