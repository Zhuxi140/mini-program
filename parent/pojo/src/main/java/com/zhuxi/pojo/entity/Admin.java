package src.main.java.com.zhuxi.pojo.entity;


import java.time.LocalDateTime;

public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private Integer status = 1;
    private LocalDateTime lastLogin;

    public Admin() {
    }

    public Admin(Integer id, String username, String password, String realName, int status, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.status = status;
        this.lastLogin = lastLogin;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
