package com.hunan.weizhang.model;

import java.io.Serializable;

/**
 * 车辆基本信息
 * 
 */
public class CarInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 车牌字母
     */
    private String shortName;
    /**
     * 车牌号(完整车牌)
     */
    private String chepaiNo;
    /**
     * 发动机号
     */
    private String engineNo;
    /**
     * 车架号
     */
    private String chejiaNo;
    
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
    public String getChejiaNo() {
        return chejiaNo;
    }
    public void setChejiaNo(String chejiaNo) {
        this.chejiaNo = chejiaNo;
    }
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
