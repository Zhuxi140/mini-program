package src.main.java.com.zhuxi.pojo.VO.User;

import src.main.java.com.zhuxi.pojo.VO.Admin.AdminUserVO;

public class UserLoginVO {
    private String token;
    private AdminUserVO adminUserVO;

    public UserLoginVO(String token, AdminUserVO adminUserVO) {
        this.token = token;
        this.adminUserVO = adminUserVO;
    }

    public UserLoginVO() {
    }

    // getter and setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AdminUserVO getUserVO() {
        return adminUserVO;
    }

    public void setUserVO(AdminUserVO adminUserVO) {
        this.adminUserVO = adminUserVO;
    }
}
