package src.main.java.com.zhuxi.pojo.DTO.Admin;

import src.main.java.com.zhuxi.pojo.entity.Role;

public class AdminLoginDTO {
    private Integer  id;
    private String username;
    private String password;
    private Role role;

    public AdminLoginDTO() {
    }

    public AdminLoginDTO(Integer id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
