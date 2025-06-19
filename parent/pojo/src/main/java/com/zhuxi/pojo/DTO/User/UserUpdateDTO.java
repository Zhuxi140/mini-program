package src.main.java.com.zhuxi.pojo.DTO.User;


public class UserUpdateDTO{

    private Long id;
    private String nickName;
    private String customAvatarOss;

    public UserUpdateDTO(String nickName,String customAvatarOss,Long id) {
        this.nickName = nickName;
        this.customAvatarOss = customAvatarOss;
        this.id = id;
    }

    public UserUpdateDTO() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCustomAvatarUrl() {
        return customAvatarOss;
    }

    public void setCustomAvatarUrl(String customAvatarUrl) {
        this.customAvatarOss = customAvatarUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
