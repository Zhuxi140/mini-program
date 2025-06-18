package src.main.java.com.zhuxi.pojo.VO;

import java.time.LocalDateTime;

public class AdminLoginVO {
    private Integer id;
    private String token;
    private String username;
    private LocalDateTime lastLogin;

    public AdminLoginVO(Integer id, String token, String username, LocalDateTime lastLogin) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.lastLogin = lastLogin;
    }

    public AdminLoginVO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
