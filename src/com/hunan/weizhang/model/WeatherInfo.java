package com.hunan.weizhang.model;

import java.io.Serializable;

import com.sprzny.hubei.R;

/**
 * 天气信息
 */
public class WeatherInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String curTemp;
    private int aqi;
    private String hightemp;
    private String lowtemp;
    private String type;

    private String xcIndex;
    private String xcDetails;

    public String getCurTemp() {
        return curTemp;
    }

    public void setCurTemp(String curTemp) {
        this.curTemp = curTemp;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public String getHightemp() {
        return hightemp;
    }

    public void setHightemp(String hightemp) {
        this.hightemp = hightemp;
    }

    public String getLowtemp() {
        return lowtemp;
    }

    public void setLowtemp(String lowtemp) {
        this.lowtemp = lowtemp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXcIndex() {
        return xcIndex;
    }

    public void setXcIndex(String xcIndex) {
        this.xcIndex = xcIndex;
    }

    public String getXcDetails() {
        return xcDetails;
    }

    public void setXcDetails(String xcDetails) {
        this.xcDetails = xcDetails;
    }

    public int getWeatherIconResourceId() {
        if (type == null) {
            return R.drawable.w_unknown;
        }
        if (type.equals("晴")) {
            return R.drawable.w_qing;
        } else if (type.equals("霾")) {
            return R.drawable.w_mai;
        } else if (type.equals("暴雪")) {
            return R.drawable.w_baoxue;
        } else if (type.equals("暴雨")) {
            return R.drawable.w_baoyu;
        } else if (type.equals("暴雨到大暴雨")) {
            return R.drawable.w_baoyu_dabaoyu;
        } else if (type.equals("大暴雨")) {
            return R.drawable.w_dabaoyu;
        } else if (type.equals("大暴雨到特大暴雨")) {
            return R.drawable.w_dabaoyu_tedabaoyu;
        } else if (type.equals("大雪")) {
            return R.drawable.w_daxue;
        } else if (type.equals("大到暴雪")) {
            return R.drawable.w_daxue_baoxue;
        } else if (type.equals("大雨")) {
            return R.drawable.w_dayu;
        } else if (type.equals("大到暴雨")) {
            return R.drawable.w_dayu_baoyu;
        } else if (type.equals("冻雨")) {
            return R.drawable.w_dongyu;
        } else if (type.equals("多云")) {
            return R.drawable.w_duoyun;
        } else if (type.equals("浮尘")) {
            return R.drawable.w_fuchen;
        } else if (type.equals("雷阵雨")) {
            return R.drawable.w_leizhenyu;
        } else if (type.equals("雷阵雨伴有冰雹")) {
            return R.drawable.w_leizhenyubingbao;
        } else if (type.equals("强沙尘暴")) {
            return R.drawable.w_qiangshachenbao;
        } else if (type.equals("沙尘暴")) {
            return R.drawable.w_shachenbao;
        } else if (type.equals("特大暴雨")) {
            return R.drawable.w_tedabaoyu;
        } else if (type.equals("雾")) {
            return R.drawable.w_wu;
        } else if (type.equals("小雪")) {
            return R.drawable.w_xiaoxue;
        } else if (type.equals("小到中雪")) {
            return R.drawable.w_xiaoxue_zhongxue;
        } else if (type.equals("小雨")) {
            return R.drawable.w_xiaoyu;
        } else if (type.equals("小到中雨")) {
            return R.drawable.w_xiaoxue_zhongxue;
        } else if (type.equals("扬沙")) {
            return R.drawable.w_yangsha;
        } else if (type.equals("阴")) {
            return R.drawable.w_yin;
        } else if (type.equals("雨夹雪")) {
            return R.drawable.w_yujiaxue;
        } else if (type.equals("阵雪")) {
            return R.drawable.w_zhenxue;
        } else if (type.equals("阵雨")) {
            return R.drawable.w_zhenyu;
        } else if (type.equals("中雪")) {
            return R.drawable.w_zhongxue;
        } else if (type.equals("中到大雪")) {
            return R.drawable.w_zhongxue_daxue;
        } else if (type.equals("中雨")) {
            return R.drawable.w_zhongyu;
        } else if (type.equals("中到大雨")) {
            return R.drawable.w_zhongyu_dayu;
        }
        
        return R.drawable.w_unknown;
    }
    
    /**
     * 空气质量
     * 
     * @return
     */
    public PmLevel getPmLevel() {
        if (aqi <= 50) {
            return PmLevel.A;
        } else if (aqi <= 100) {
            return PmLevel.B;
        } else if (aqi <= 150) {
            return PmLevel.C;
        } else if (aqi <= 200) {
            return PmLevel.D;
        } else if (aqi <= 300) {
            return PmLevel.E;
        } else {
            return PmLevel.F;
        }
    }

    public enum PmLevel {
        A("优", R.color.csy_weather_a), 
        B("良", R.color.csy_weather_b), 
        C("轻度污染", R.color.csy_weather_c), 
        D("中度污染", R.color.csy_weather_d), 
        E("重度污染", R.color.csy_weather_e), 
        F("严重污染", R.color.csy_weather_f);

        private String text;
        private int color;

        private PmLevel(String text, int color) {
            this.text = text;
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public int getColor() {
            return color;
        }
    }
}
