package com.hunan.weizhang.model;

import java.io.Serializable;
import java.util.List;

/**
 * 违章消息对象
 *
 */
public class WeizhangMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回码
     */
    private String code;
    /**
     * 违章详情
     */
    private List<WeizhangInfo> data;
    
    /**
     * 总扣分
     */
    private int totalScores;
    /**
     * 总罚款金额
     */
    private int totalFkje;
    /**
     * 未处理数量
     */
    private int untreatedCount;
    /**
     * 查询时间
     */
    private long searchTimestamp;
    /**
     * 车辆信息
     */
    private CarInfo carInfo;
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public List<WeizhangInfo> getData() {
        return data;
    }
    public void setData(List<WeizhangInfo> data) {
        this.data = data;
    }
    public int getTotalScores() {
        return totalScores;
    }
    public void setTotalScores(int totalScores) {
        this.totalScores = totalScores;
    }
    public int getTotalFkje() {
        return totalFkje;
    }
    public void setTotalFkje(int totalFkje) {
        this.totalFkje = totalFkje;
    }
    public int getUntreatedCount() {
        return untreatedCount;
    }
    public void setUntreatedCount(int untreatedCount) {
        this.untreatedCount = untreatedCount;
    }
    public long getSearchTimestamp() {
        return searchTimestamp;
    }
    public void setSearchTimestamp(long searchTimestamp) {
        this.searchTimestamp = searchTimestamp;
    }
    public CarInfo getCarInfo() {
        return carInfo;
    }
    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
    }
}
