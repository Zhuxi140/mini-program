package com.zhuxi.pojo.DTO.User;


public class LoginMQDTO {
    private Long id;
    private String openId;
    private String sessionKey;
    private String name;
    private String avatar;

    public LoginMQDTO() {
    }

    public LoginMQDTO(Long id, String openId, String sessionKey, String name, String avatar) {
        this.id = id;
        this.openId = openId;
        this.sessionKey = sessionKey;
        this.name = name;
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
