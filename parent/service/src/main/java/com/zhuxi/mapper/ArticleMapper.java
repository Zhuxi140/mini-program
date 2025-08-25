package com.zhuxi.mapper;

import com.zhuxi.pojo.VO.Article.AdminArticleVO;
import org.apache.ibatis.annotations.*;
import com.zhuxi.pojo.DTO.article.ArticleInsertOrUpdateDTO;
import com.zhuxi.pojo.DTO.article.ArticleOssUpdateDTO;
import com.zhuxi.pojo.VO.Article.ArticleDetailVO;
import com.zhuxi.pojo.VO.Article.ArticleVO;

import java.util.List;

@Mapper
public interface ArticleMapper {


    @Select("""
        SELECT id,
               title,
               type,
               content_oss_key,
               content_images,
               created_at
        FROM article
        WHERE id = #{id}""")
    @Result(property = "contentImages", column = "content_images", typeHandler = com.zhuxi.handler.TypeHandler.class)
    ArticleDetailVO getArticleDetailById(Long id);

    List<AdminArticleVO> getListArticleByAdmin(Long lastId, Integer pageSize, Integer type);

    List<ArticleVO> getListArticleDESC(Long lastId, Integer pageSize, Integer type);

    @Insert("""
        INSERT INTO article(title,status, type)
        VALUE (#{title},#{status},#{type})
        """)
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
        Boolean insertArticle(ArticleInsertOrUpdateDTO articleInsertOrUpdateDTO);


    int updateArticle(ArticleInsertOrUpdateDTO aIOUDto,Long id);

    // 判断文章是否存在
    @Select("SELECT COUNT(*) FROM article WHERE id = #{id}")
    int isExist(Long id);

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
