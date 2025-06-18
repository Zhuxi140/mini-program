package src.main.java.com.zhuxi.pojo.VO;

public class UserLoginVO {
    private String token;
    private UserVO userVO;

    public UserLoginVO(String token, UserVO userVO) {
        this.token = token;
        this.userVO = userVO;
    }

    // getter and setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserVO getUserVO() {
        return userVO;
    }

    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }
}
