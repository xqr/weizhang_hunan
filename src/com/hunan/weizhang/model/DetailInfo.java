package com.hunan.weizhang.model;

import java.io.Serializable;

public class DetailInfo implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int distance;
    private String tag;
    private LatLng navi_location;
    private String type;
    private String detail_url;
    private String overall_rating;
    private String service_rating;
    private String environment_rating;
    private String image_num;
    private String comment_num;
    private String price;
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDetail_url() {
        return detail_url;
    }
    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }
    public String getOverall_rating() {
        return overall_rating;
    }
    public void setOverall_rating(String overall_rating) {
        this.overall_rating = overall_rating;
    }
    public String getService_rating() {
        return service_rating;
    }
    public void setService_rating(String service_rating) {
        this.service_rating = service_rating;
    }
    public String getEnvironment_rating() {
        return environment_rating;
    }
    public void setEnvironment_rating(String environment_rating) {
        this.environment_rating = environment_rating;
    }
    public String getImage_num() {
        return image_num;
    }
    public void setImage_num(String image_num) {
        this.image_num = image_num;
    }
    public String getComment_num() {
        return comment_num;
    }
    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    
    public LatLng getNavi_location() {
        return navi_location;
    }
    public void setNavi_location(LatLng navi_location) {
        this.navi_location = navi_location;
    }
}
