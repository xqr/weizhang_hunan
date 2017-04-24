package com.hunan.weizhang.model;

import com.sprzny.quanguo.R;

public enum ShortName {
    HUNAN(R.drawable.hunan, "湖南", "湘",  true, false, true, false),
    HUBEI(R.drawable.hubei, "湖北", "鄂", false, true, false, true),
    SHANGHAI(R.drawable.shanghai, "上海", "沪", true, false, false, false),
    BEIJING(R.drawable.beijing, "北京", "京", true, false, false, false),
    TIANJIN(R.drawable.tianjin, "天津", "津", true, true, false, false),
    SICHUAN(R.drawable.sichuan, "四川", "川", true, true, false, false),
    GANSU(R.drawable.gansu, "甘肃", "甘", true, true, false, false),
    JIANGXI(R.drawable.jiangxi, "江西", "赣", true, true, false, false),
    GUANGXI(R.drawable.guangxi, "广西", "桂", true, true, false, false),
    GUIZHOU(R.drawable.guizhou, "贵州", "贵", true, true, false, false),
    HEILONGJIANG(R.drawable.heilongjiang, "黑龙江", "黑", true, true, false, false),
    JILIN(R.drawable.jilin, "吉林", "吉", true, true, false, false),
    HEBEI(R.drawable.hebei, "河北", "冀", true, true, false, false),
    SHANXI(R.drawable.shanxi, "山西", "晋", true, true, false, false),
    LIAONING(R.drawable.liaoning, "辽宁", "辽", true, true, false, false),
    SHANDONG(R.drawable.shandong, "山东", "鲁", true, true, false, false),
    NEIMENGGU(R.drawable.neimenggu, "内蒙古", "蒙", true, true, false, false),
    FUJIAN(R.drawable.fujian, "福建", "闽", true, true, false, false),
    NINGXIA(R.drawable.ningxia, "宁夏", "宁", true, true, false, false),
    QINGHAI(R.drawable.qinghai, "青海", "青", true, true, false, false),
    HAINAN(R.drawable.hainan, "海南", "琼", true, true, false, false),
    SHANXI2(R.drawable.shanxi2, "陕西", "陕", true, true, false, false),
    JIANGSU(R.drawable.jiangsu, "江苏", "苏", true, true, false, false),
    ANHUI(R.drawable.anhui, "安徽", "皖", true, true, false, false),
    XINJIANG(R.drawable.xinjiang, "新疆", "新", true, true, false, false),
    CHONGQIN(R.drawable.chongqing, "重庆", "渝", true, true, false, false),
    HENAN(R.drawable.henan, "河南", "豫", true, true, false, false),
    GUANGDONG(R.drawable.guangdong, "广东", "粤", true, true, false, false),
    YUNNAN(R.drawable.yunnan, "云南", "云", true, true, false, false),
    XIZANG(R.drawable.xizang, "西藏", "藏", true, true, false, false),
    ZHEJIANG(R.drawable.zhejiang, "浙江", "浙", true, true, false, false); 
    
    private ShortName(int bannerId, String provinceName, String name, boolean isNeedEngine, 
            boolean isNeedchejia, boolean isShortEngine, boolean isShortchejia) {
        this.bannerId = bannerId;
        this.provinceName = provinceName;
        this.name = name;
        this.isNeedEngine = isNeedEngine;
        this.isNeedchejia = isNeedchejia;
        this.isShortEngine = isShortEngine;
        this.isShortchejia = isShortchejia;
    }
    
    private int bannerId;
    
    public int getBannerId() {
        return bannerId;
    }

    public void setBannerId(int bannerId) {
        this.bannerId = bannerId;
    }

    private String provinceName;

    private String name;
    
    private boolean isNeedEngine;
    
    private boolean isNeedchejia;
    
    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    
    public boolean isNeedchejia() {
        return isNeedchejia;
    }

    public void setNeedchejia(boolean isNeedchejia) {
        this.isNeedchejia = isNeedchejia;
    }

    public boolean isShortchejia() {
        return isShortchejia;
    }

    public void setShortchejia(boolean isShortchejia) {
        this.isShortchejia = isShortchejia;
    }

    private boolean isShortEngine;
    
    private boolean isShortchejia;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNeedEngine() {
        return isNeedEngine;
    }

    public void setNeedEngine(boolean isNeedEngine) {
        this.isNeedEngine = isNeedEngine;
    }

    public boolean isShortEngine() {
        return isShortEngine;
    }

    public void setShortEngine(boolean isShortEngine) {
        this.isShortEngine = isShortEngine;
    }
}
