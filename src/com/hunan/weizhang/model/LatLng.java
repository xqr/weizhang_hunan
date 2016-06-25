package com.hunan.weizhang.model;

import java.io.Serializable;

/**
 * 坐标对象
 */
public class LatLng implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double lat;
    private double lng;
    
    public LatLng() {
        
    }
    
    public LatLng(double latitude, double longitude) {
        this.lat = latitude;
        this.lng = longitude;
    }
    
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
