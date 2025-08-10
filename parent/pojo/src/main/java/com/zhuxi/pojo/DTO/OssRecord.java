package com.zhuxi.pojo.DTO;

import java.time.LocalDateTime;

public class OssRecord {

    private Long id;

    private String objectName;

    private String bucketName;

    private Long size;

    private String mimeType;

    private String categoryType;

    private LocalDateTime uploadTime;

    private String scanStatus;

    private String description;

    private String tags;

    private Integer accessCount = 0;

    private String cdnUrl;

    public OssRecord(Long id, String objectName, String bucketName, Long size, String mimeType, String categoryType, LocalDateTime uploadTime, String scanStatus, String description, String tags, Integer accessCount, String cdnUrl) {
        this.id = id;
        this.objectName = objectName;
        this.bucketName = bucketName;
        this.size = size;
        this.mimeType = mimeType;
        this.categoryType = categoryType;
        this.uploadTime = uploadTime;
        this.scanStatus = scanStatus;
        this.description = description;
        this.tags = tags;
        this.accessCount = accessCount;
        this.cdnUrl = cdnUrl;
    }

    public OssRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(String scanStatus) {
        this.scanStatus = scanStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

    public String getCdnUrl() {
        return cdnUrl;
    }

    public void setCdnUrl(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }
}
