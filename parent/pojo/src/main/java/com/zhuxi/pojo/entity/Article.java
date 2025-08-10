package com.zhuxi.pojo.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Article {

    private Long id;
    private String title; // 标题
    private String contentOssKey; // 文章内容oss key
    private int type;          // 1.公告 2.文章
    private Integer status;  // 1.草稿  2.发布
    private String coverOss;  // 封面
    private List<String> contentImages; // 文章内容图片OSS
    private LocalDateTime createdAt; // 发布时间

    public Article(Long id, String title, String contentOssKey,Integer status, int type, String coverOss, List<String> contentImages,LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.contentOssKey = contentOssKey;
        this.status = status;
        this.type = type;
        this.coverOss = coverOss;
        this.contentImages = contentImages;
        this.createdAt = createdAt;
    }

    public Article() {
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

    public String getContentOssKey() {
        return contentOssKey;
    }

    public void setContentOssKey(String contentOssKey) {
        this.contentOssKey = contentOssKey;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
