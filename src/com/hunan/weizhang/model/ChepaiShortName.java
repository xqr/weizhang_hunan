package com.hunan.weizhang.model;

public class ChepaiShortName {
    
    // 全部车牌信息
    public static String[] allShortName = new String[] { "京", "津", "沪", "川", "鄂", "甘", "赣", "桂", "贵", "黑",
        "吉", "冀", "晋", "辽", "鲁", "蒙", "闽", "宁", "青", "琼", "陕", "苏",
        "皖", "湘", "新", "渝", "豫", "粤", "云", "藏", "浙", ""};
    
    /**
     *  省份与车牌简称对应关系
     * 
     * @param provName
     * @return
     */
    public static ShortName getShortName(String provName) {
        if (provName == null) {
            return null;
        }
        
        for (ShortName shortName : ShortName.values()) {
            if (shortName.getProvinceName().equals(provName)) {
                return shortName;
            }
        }
        return ShortName.HUBEI;
    }
    
    /**
     * 通过车牌简称获取信息
     * 
     * @param shortName
     * @return
     */
    public static ShortName  getShortNameByName(String shortName) {
        if (shortName == null) {
            return null;
        }
        
        for (ShortName item : ShortName.values()) {
            if (item.getName().equals(shortName)) {
                return item;
            }
        }
        return ShortName.HUBEI;
    }
}