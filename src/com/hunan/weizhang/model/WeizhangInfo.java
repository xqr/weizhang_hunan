package com.hunan.weizhang.model;

import java.io.Serializable;

/**
 * 违章详细信息
 */
public class WeizhangInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String dsr;
    private String xh;
    private String zt;
    /**
     * 违法时间
     */
    private String wfsj;
    /**
     * 违法地址(必填)
     */
    private String wfdz;
    private String wfdm;
    /**
     * 违法行为
     */
    private String wfxw;
    /**
     * 扣分
     */
    private String wfjfs;
    /**
     * 罚款金额
     */
    private String fkje;
    private String znj;
    private String jllx;
    private String fltw;
    private String wfgd;
    private String wfjszh;
    private String fxjg;
    private String wsbj;
    
    public WeizhangInfo() {
        
    }
    
    /**
     * 基本信息
     * 
     * @param wfsj 违法事件
     * @param wfdz 违法地址
     * @param wfxw 违法行为
     * @param wfjfs 扣分
     * @param fkje 罚款金额
     */
    public WeizhangInfo(String wfsj, String wfdz, String wfxw, String wfjfs, String fkje) {
        this.wfsj = wfsj;
        this.wfdz = wfdz;
        this.wfxw = wfxw;
        this.wfjfs = wfjfs;
        this.fkje = fkje;
    }
    
    
    public String getDsr() {
        return dsr;
    }
    public void setDsr(String dsr) {
        this.dsr = dsr;
    }
    public String getXh() {
        return xh;
    }
    public void setXh(String xh) {
        this.xh = xh;
    }
    public String getZt() {
        return zt;
    }
    public void setZt(String zt) {
        this.zt = zt;
    }
    public String getWfsj() {
        return wfsj;
    }
    public void setWfsj(String wfsj) {
        this.wfsj = wfsj;
    }
    public String getWfdz() {
        return wfdz;
    }
    public void setWfdz(String wfdz) {
        this.wfdz = wfdz;
    }
    public String getWfdm() {
        return wfdm;
    }
    public void setWfdm(String wfdm) {
        this.wfdm = wfdm;
    }
    public String getWfxw() {
        return wfxw;
    }
    public void setWfxw(String wfxw) {
        this.wfxw = wfxw;
    }
    public String getWfjfs() {
        return wfjfs;
    }
    public void setWfjfs(String wfjfs) {
        this.wfjfs = wfjfs;
    }
    public String getFkje() {
        return fkje;
    }
    public void setFkje(String fkje) {
        this.fkje = fkje;
    }
    public String getZnj() {
        return znj;
    }
    public void setZnj(String znj) {
        this.znj = znj;
    }
    public String getJllx() {
        return jllx;
    }
    public void setJllx(String jllx) {
        this.jllx = jllx;
    }
    public String getFltw() {
        return fltw;
    }
    public void setFltw(String fltw) {
        this.fltw = fltw;
    }
    public String getWfgd() {
        return wfgd;
    }
    public void setWfgd(String wfgd) {
        this.wfgd = wfgd;
    }
    public String getWfjszh() {
        return wfjszh;
    }
    public void setWfjszh(String wfjszh) {
        this.wfjszh = wfjszh;
    }
    public String getFxjg() {
        return fxjg;
    }
    public void setFxjg(String fxjg) {
        this.fxjg = fxjg;
    }
    public String getWsbj() {
        return wsbj;
    }
    public void setWsbj(String wsbj) {
        this.wsbj = wsbj;
    }
}
