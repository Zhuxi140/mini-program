package src.main.java.com.zhuxi.pojo.VO.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserBasicVO {

    private String name;
    private String avatar;
    private String token;
    @JsonIgnore
    private String openid;

    public UserBasicVO() {
    }

    public UserBasicVO(String name, String avatar, String token, String openid) {
        this.name = name;
        this.avatar = avatar;
        this.token = token;
        this.openid = openid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
