package com.zhuxi.Result.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneResult {

    @JsonProperty("errcode")
    private int errCode;
    @JsonProperty("errmsg")
    private String errMsg;
    @JsonProperty("phone_info")
    private PhoneInfoResult phoneInfo;

    public PhoneResult() {
    }

    public PhoneResult(int errCode, String errMsg, PhoneInfoResult phoneInfo) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.phoneInfo = phoneInfo;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public PhoneInfoResult getPhoneInfo() {
        return phoneInfo;
    }

    public void setPhoneInfo(PhoneInfoResult phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

    @Override
    public String toString() {
        return "PhoneResult{" +
                "errCode=" + errCode +
                ", errMsg='" + errMsg + '\'' +
                ", phoneInfo=" + phoneInfo.toString() +
                '}';
    }
}
