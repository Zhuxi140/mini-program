package src.main.java.com.zhuxi.pojo.VO;

public class UserVO {
    String nickName;
    String wxNickName;
    String wxAvatarUrl;
    String customAvatarUrl;
    Boolean hasWechat;
    Boolean hasPhone;

    public UserVO(Integer id, String nickName, String wxNickName, String wxAvatarUrl, Boolean hasWechat, Boolean hasPhone) {
        this.nickName = nickName;
        this.wxNickName = wxNickName;
        this.wxAvatarUrl = wxAvatarUrl;
        this.hasWechat = hasWechat;
        this.hasPhone = hasPhone;
    }

    /**
     * 显示名称
     */
    public String displayName() {
        if (hasWechat != null) {
            if (hasPhone != null) {
                if (nickName != null && !nickName.equals("金银花用户"))
                    return nickName;
                return wxNickName;
            } else
                return wxNickName;
        }

        return wxNickName;
    }


    /**
     *  显示头像
     */
    public String displayAvatar() {
        if (hasWechat != null) {
            if (hasPhone != null) {
                if (customAvatarUrl != null)
                    return customAvatarUrl;
                return wxAvatarUrl;
            }
        }

        return wxAvatarUrl;
    }


        // getter or setter
        public String getNickName () {
            return nickName;
        }

        public void setNickName (String nickName){
            this.nickName = nickName;
        }

        public String getWxNickName () {
            return wxNickName;
        }

        public void setWxNickName (String wxNickName){
            this.wxNickName = wxNickName;
        }

        public String getWxAvatarUrl () {
            return wxAvatarUrl;
        }

        public void setWxAvatarUrl (String wxAvatarUrl){
            this.wxAvatarUrl = wxAvatarUrl;
        }

        public Boolean getHasWechat () {
            return hasWechat;
        }

        public void setHasWechat (Boolean hasWechat){
            this.hasWechat = hasWechat;
        }

        public Boolean getHasPhone () {
            return hasPhone;
        }

        public void setHasPhone (Boolean hasPhone){
            this.hasPhone = hasPhone;
        }
}
