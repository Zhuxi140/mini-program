package src.main.java.com.zhuxi.pojo.VO;

public class AdminUserVO {
    private Long id;
    private String phone;
    private String nickName;
    private String wxNickName;
    private String wxAvatarUrl;
    private String status;
    private String customAvatarOss;

    public AdminUserVO(Long id, String nickName, String wxNickName, String wxAvatarUrl, String customAvatarOss, String status, String phone) {
        this.id = id;
        this.customAvatarOss = customAvatarOss;
        this.nickName = nickName;
        this.wxNickName = wxNickName;
        this.wxAvatarUrl = wxAvatarUrl;
        this.status = status;
        this.phone = phone;

    }

    // getter or setter
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomAvatarUrl() {
        return customAvatarOss;
    }

    public void setCustomAvatarUrl(String customAvatarUrl) {
        this.customAvatarOss = customAvatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomAvatarOss() {
        return customAvatarOss;
    }

    public void setCustomAvatarOss(String customAvatarOss) {
        this.customAvatarOss = customAvatarOss;
    }
}
