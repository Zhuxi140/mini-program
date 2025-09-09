package com.zhuxi.pojo.VO.Article;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleDetailVO {

    @Schema(description = "文章id")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "文章类型(1为公告，2为文章/新闻")
    private String type;
    @Schema(description = "包含HTML的文本")
    private String content;
    @Schema(description = "文章内容图片OSS")
    private List<String> contentImages;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "文章发布时间")
    private LocalDateTime createdAt;

    public ArticleDetailVO() {
    }

    public ArticleDetailVO(Long id, String title, String type, String content, List<String> contentImages, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.content = content;
        this.contentImages = contentImages;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public List<String> getContentImages() {
        return contentImages;
    }

    public void setContentImages(List<String> contentImages) {
        this.contentImages = contentImages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
