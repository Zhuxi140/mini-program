package com.zhuxi.Result.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhuxi.Result.wechat.waterMark;

public class PhoneInfoResult {
        private String phoneNumber;
        private String purePhoneNumber;
        private String countryCode;
        @JsonProperty("watermark")
        private waterMark waterMark;

        public PhoneInfoResult() {
        }

        public PhoneInfoResult(String phoneNumber, String purePhoneNumber, String countryCode, waterMark waterMark) {
                this.phoneNumber = phoneNumber;
                this.purePhoneNumber = purePhoneNumber;
                this.countryCode = countryCode;
                this.waterMark = waterMark;
        }

        public String getPhoneNumber() {
                return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
        }

        public String getPurePhoneNumber() {
                return purePhoneNumber;
        }

        public void setPurePhoneNumber(String purePhoneNumber) {
                this.purePhoneNumber = purePhoneNumber;
        }

        public String getCountryCode() {
                return countryCode;
        }

        public void setCountryCode(String countryCode) {
                this.countryCode = countryCode;
        }

        public com.zhuxi.Result.wechat.waterMark getWaterMark() {
                return waterMark;
        }

        public void setWaterMark(com.zhuxi.Result.wechat.waterMark waterMark) {
                this.waterMark = waterMark;
        }

        @Override
        public String toString() {
                return "PhoneInfoResult{" +
                        "phoneNumber='" + phoneNumber + '\'' +
                        ", purePhoneNumber='" + purePhoneNumber + '\'' +
                        ", countryCode='" + countryCode + '\'' +
                        ", waterMark=" + waterMark.toString() +
                        '}';
        }
}
