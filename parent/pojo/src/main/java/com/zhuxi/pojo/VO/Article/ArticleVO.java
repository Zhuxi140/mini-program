package com.zhuxi.pojo.VO.Article;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ArticleVO {

    @Schema(description = "文章id")
    private Long id;
    @Schema(description = "文章标题")
    private String title;
    @Schema(description = "文章类型(1为公告，2为新闻/文章)")
    private int type;
    @Schema(description = "文章封面oss")
    private String coverOss;
    @Schema(description = "文章发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    public ArticleVO(Long id, String title, int type, String coverOss, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.coverOss = coverOss;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCoverOss() {
        return coverOss;
    }

    public void setCoverOss(String coverOss) {
        this.coverOss = coverOss;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
