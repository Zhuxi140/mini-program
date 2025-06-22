package src.main.java.com.zhuxi.pojo.DTO.User;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserAddressDTO {

    @Schema(description = "地址id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;
    @Schema(description = "收件人",requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipient;
    @Schema(description = "手机号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    @Schema(description = "省份",requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;
    @Schema(description = "城市",requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;
    @Schema(description = "区县",requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;
    @Schema(description = "详细地址",requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;
    @Schema(description = "是否默认地址(默认为否)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isDefault = 0;

    public UserAddressDTO(Long id,String recipient, String phone, String province, String city, String district, String detail, Integer isDefault) {
        this.id = id;
        this.recipient = recipient;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.isDefault = isDefault;
    }

    public UserAddressDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }


}
