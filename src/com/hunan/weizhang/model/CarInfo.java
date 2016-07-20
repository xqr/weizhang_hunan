package com.hunan.weizhang.model;

import java.io.Serializable;

/**
 * 车辆基本信息
 * 
 */
public class CarInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 车牌号
     */
    private String chepaiNo;
    /**
     * 发动机号
     */
    private String engineNo;
    /**
     * 号牌种类
     */
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
