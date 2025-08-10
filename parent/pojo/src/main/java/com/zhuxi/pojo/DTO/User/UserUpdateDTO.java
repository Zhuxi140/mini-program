package com.zhuxi.pojo.DTO.User;


import io.swagger.v3.oas.annotations.media.Schema;

public class UserUpdateDTO{

    @Schema(description = "用户id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;
    @Schema(description = "昵称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickName;
    @Schema(description = "自定义头像oss地址",requiredMode = Schema.RequiredMode.REQUIRED)
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

    public String getCustomAvatarOss() {
        return customAvatarOss;
    }

    public void setCustomAvatarOss(String customAvatarOss) {
        this.customAvatarOss = customAvatarOss;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
