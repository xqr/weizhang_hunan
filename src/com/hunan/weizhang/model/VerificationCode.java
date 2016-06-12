package com.hunan.weizhang.model;

import java.io.Serializable;

/**
 * 验证码
 */
public class VerificationCode implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 验证码图片
     */
    private String tpyzm;
    
    /**
     * token
     */
    private String token;
    
    /**
     * 验证码
     */
    private String randCode;

    public String getTpyzm() {
        return tpyzm;
    }

    public void setTpyzm(String tpyzm) {
        this.tpyzm = tpyzm;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRandCode() {
        return randCode;
    }

    public void setRandCode(String randCode) {
        this.randCode = randCode;
    }
}
