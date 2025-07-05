package src.main.java.com.zhuxi.pojo.DTO.article;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ArticleOssUpdateDTO {
    @Schema(description = "文章id")
    private Long articleId;
    @Schema(description = "文章内容oss")
    private String contentOssKey;
    @Schema(description = "文章封面oss")
    private String coverOss;
    @Schema(description = "文章内使用照片oss")
    private List<String> contentImages;

    public ArticleOssUpdateDTO(Long articleId,String contentOssKey, String coverOss, List<String> contentImages) {
        this.articleId = articleId;
        this.contentOssKey = contentOssKey;
        this.coverOss = coverOss;
        this.contentImages = contentImages;
    }

    public ArticleOssUpdateDTO() {
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContentOssKey() {
        return contentOssKey;
    }

    public void setContentOssKey(String contentOssKey) {
        this.contentOssKey = contentOssKey;
    }

    public String getCoverOss() {
        return coverOss;
    }

    public void setCoverOss(String coverOss) {
        this.coverOss = coverOss;
    }

    public List<String> getContentImages() {
        return contentImages;
    }

    public void setContentImages(List<String> contentImages) {
        this.contentImages = contentImages;
    }
}
