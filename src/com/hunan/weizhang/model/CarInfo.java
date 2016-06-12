package com.hunan.weizhang.model;

import java.io.Serializable;

public class CarInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String chepaiNo;
    private String engineNo;
    private String haopaiType;

    public String getHaopaiType() {
        return haopaiType;
    }
    public void setHaopaiType(String haopaiType) {
        this.haopaiType = haopaiType;
    }
    
    public String getChepaiNo() {
        return chepaiNo;
    }
    public void setChepaiNo(String chepaiNo) {
        this.chepaiNo = chepaiNo;
    }
    public String getEngineNo() {
        return engineNo;
    }
    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }
}
