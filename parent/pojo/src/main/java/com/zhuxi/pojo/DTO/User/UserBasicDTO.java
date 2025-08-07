package src.main.java.com.zhuxi.pojo.DTO.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserBasicDTO {

    @JsonIgnore
    private Long id;
    @Schema(description = "用户名",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "用户头像地址",requiredMode = Schema.RequiredMode.REQUIRED)
    private String avatar;
    @JsonIgnore
    private String openId;

    public UserBasicDTO() {
    }

    public UserBasicDTO(Long id,String name, String avatar, String openId) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.openId = openId;
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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
