package src.main.java.com.zhuxi.pojo.DTO.User;

import src.main.java.com.zhuxi.pojo.entity.Role;

public class UserLoginDTO {

    private Long id;
    private String openId;
    private Role role;
    private String token;

    public UserLoginDTO() {
    }

    public UserLoginDTO(String openId, Role role, String token, Long id) {
        this.openId = openId;
        this.role = role;
        this.token = token;
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
