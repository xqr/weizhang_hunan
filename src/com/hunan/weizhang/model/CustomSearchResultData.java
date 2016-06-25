package com.hunan.weizhang.model;

public class CustomSearchResultData {
    
    private String name;
    private LatLng location;
    private String address;
    private String street_id;
    private String telephone;
    private int detail;
    private String uid;
    private DetailInfo detail_info;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getStreet_id() {
        return street_id;
    }
    public void setStreet_id(String street_id) {
        this.street_id = street_id;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public int getDetail() {
        return detail;
    }
    public void setDetail(int detail) {
        this.detail = detail;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public DetailInfo getDetail_info() {
        return detail_info;
    }
    public void setDetail_info(DetailInfo detail_info) {
        this.detail_info = detail_info;
    }
    public LatLng getLocation() {
        return location;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
}
