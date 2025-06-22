package src.main.java.com.zhuxi.pojo.entity;


public class User {
    private Long id;
    private String openId;          //微信标识
    private String phone;           //手机号
    private String nickName;        // 自定义昵称
    private String wxNickName;      // 微信昵称
    private String wxAvatarUrl;     // 微信头像url
    private String customAvatarUrl; // 自定义头像url
    private Integer status;          // 状态           1表示启用  0表示禁用
    private Long addressId;

    public User(Long id, String openId, String phone, String nickName, String wxNickName, String wxAvatarUrl, String customAvatarUrl, Integer status, Long addressId) {
        this.id = id;
        this.openId = openId;
        this.phone = phone;
        this.nickName = nickName;
        this.wxNickName = wxNickName;
        this.wxAvatarUrl = wxAvatarUrl;
        this.customAvatarUrl = customAvatarUrl;
        this.status = status;
        this.addressId = addressId;
    }

    public User() {
    }

    //getter or setter
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
