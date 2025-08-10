package com.zhuxi.pojo.entity;

public class Supplier {

    private Integer id;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private Integer rating;
    private Integer isActive;

    public Supplier() {
    }

    public Supplier(Integer id, String name, String contact, String phone, String address, Integer rating, Integer isActive) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.phone = phone;
        this.address = address;
        this.rating = rating;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
