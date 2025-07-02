package src.main.java.com.zhuxi.pojo.DTO.article;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ArticleInsertOrUpdateDTO {

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "内容oss key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contentOssKey;

    @Schema(description = "状态(1草稿 2 发布  默认不设置为1草稿状态)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    @Schema(description = "类型(1公告 2 新闻/文章", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "封面oss", requiredMode = Schema.RequiredMode.REQUIRED)
    private String coverOss;

    @Schema(description = "内容图片oss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> contentImages;

    public ArticleInsertOrUpdateDTO(String title, String contentOssKey, Integer status, Integer type, String coverOss, List<String> contentImages) {
        this.title = title;
        this.contentOssKey = contentOssKey;
        this.status = status;
        this.type = type;
        this.coverOss = coverOss;
        this.contentImages = contentImages;
    }

    public ArticleInsertOrUpdateDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentOssKey() {
        return contentOssKey;
    }

    public void setContentOssKey(String contentOssKey) {
        this.contentOssKey = contentOssKey;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
