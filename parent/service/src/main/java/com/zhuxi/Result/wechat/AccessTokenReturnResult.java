package com.zhuxi.Result.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenReturnResult {

    @JsonProperty("errcode")
    private int errCode;
    @JsonProperty("errmsg")
    private String errMsg;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private String expiresIn;

    public AccessTokenReturnResult() {
    }

    public AccessTokenReturnResult(int errCode, String errMsg, String accessToken, String expiresIn) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }



    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}
