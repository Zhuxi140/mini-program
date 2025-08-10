package com.zhuxi.pojo.DTO.User;

public class WechatLoginDTO {

    private String phone;
    private String nickName;
    private String wxNickName;
    private String wxAvatarUrl;
    private String customAvatarUrl;

    public WechatLoginDTO(String phone, String nickName, String wxNickName, String wxAvatarUrl, String customAvatarUrl) {
        this.phone = phone;
        this.nickName = nickName;
        this.wxNickName = wxNickName;
        this.wxAvatarUrl = wxAvatarUrl;
        this.customAvatarUrl = customAvatarUrl;
    }

    public WechatLoginDTO() {
    }

    // getter or setter
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getWxNickName() {
        return wxNickName;
    }

    public void setWxNickName(String wxNickName) {
        this.wxNickName = wxNickName;
    }

    public String getWxAvatarUrl() {
        return wxAvatarUrl;
    }

    public void setWxAvatarUrl(String wxAvatarUrl) {
        this.wxAvatarUrl = wxAvatarUrl;
    }

    public String getCustomAvatarUrl() {
        return customAvatarUrl;
    }

    public void setCustomAvatarUrl(String customAvatarUrl) {
        this.customAvatarUrl = customAvatarUrl;
    }
}
