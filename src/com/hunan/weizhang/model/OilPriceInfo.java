package com.hunan.weizhang.model;

/**
 * 油价信息
 *
 */
public class OilPriceInfo {
    private String prov;
    private String p90;
    private String p0;
    private String p97;
    private String ct;
    private String p93;
    
    public String getP90() {
        return p90;
    }
    public void setP90(String p90) {
        this.p90 = p90;
    }
    public String getP0() {
        return p0;
    }
    public void setP0(String p0) {
        this.p0 = p0;
    }
    public String getP97() {
        return p97;
    }
    public void setP97(String p97) {
        this.p97 = p97;
    }
    public String getCt() {
        return ct;
    }
    public void setCt(String ct) {
        this.ct = ct;
    }
    public String getP93() {
        return p93;
    }
    public void setP93(String p93) {
        this.p93 = p93;
    }
    public String getProv() {
        return prov;
    }
    public void setProv(String prov) {
        this.prov = prov;
    }
    
    private String[] provList = new String[] {"上海", "北京"};
    
    /**
     * 获取油价显示(部分城市需要特殊处理)
     * 
     * @return
     */
    public String getShowText() {
        for (String name : provList) {
            if (name.equals(prov)) {
                return String.format("<font color='#0099FF'>89#</font> %s <font color='#EBEBEB'>|</font> <font color='#FF0000'>92#</font> %s <font color='#EBEBEB'>|</font>  <font color='#008000'>95#</font> %s <font color='#EBEBEB'>|</font> <font color='#999999'>0#</font> %s ", 
                        p90.split("\\(")[0], p93.split("\\(")[0], p97.split("\\(")[0], p0);
            }
        }
        return String.format("<font color='#0099FF'>90#</font> %s <font color='#EBEBEB'>|</font> <font color='#FF0000'>93#</font> %s <font color='#EBEBEB'>|</font>  <font color='#008000'>97#</font> %s <font color='#EBEBEB'>|</font> <font color='#999999'>0#</font> %s ", 
                p90, p93, p97, p0);
    }
}
