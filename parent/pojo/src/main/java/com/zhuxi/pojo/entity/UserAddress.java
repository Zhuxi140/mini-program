package src.main.java.com.zhuxi.pojo.entity;

import java.time.LocalDateTime;

public class UserAddress {

    private Long id;
    private Long userId;
    private String recipient; // 收货人
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private int isDefault;
    private LocalDateTime createdAt;

    public UserAddress(Long id, Long userId, String recipient, String phone, String city, String district, String detail, int isDefault, LocalDateTime createdAt,String province) {
        this.id = id;
        this.userId = userId;
        this.recipient = recipient;
        this.phone = phone;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.province = province;
    }

    public UserAddress() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
