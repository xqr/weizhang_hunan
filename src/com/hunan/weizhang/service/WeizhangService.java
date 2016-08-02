package com.hunan.weizhang.service;

import java.util.Date;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangMessage;

public abstract class WeizhangService {
    
    private WeizhangHistoryService weizhangHistoryService;
    
    public WeizhangService(WeizhangHistoryService weizhangHistoryService) {
        this.weizhangHistoryService = weizhangHistoryService;
    }
    
    /**
     * 查询违章信息
     * 
     * @param weizhangMessage
     * @return
     */
    public WeizhangMessage searchWeizhangMessage(WeizhangMessage weizhangMessage,
            VerificationCode verificationCode) {
        if (weizhangMessage == null) {
            return null;
        }
        
        // 信息缓存8个小时
        if((weizhangMessage.getSearchTimestamp() + 1000 * 60 * 60 * 8) >= new Date().getTime()) {
            return weizhangMessage;
        }
        
        boolean isFromHistory = weizhangMessage.getSearchTimestamp() != 0;
        
        // 从服务器上查询结果
        WeizhangMessage newWeizhangMessage = searchWeizhangMessage(weizhangMessage.getCarInfo(), 
                isFromHistory, verificationCode);
        
        // 如果查询失败
        if (newWeizhangMessage == null 
                || !WeizhangMessage.SUCCESS_CODE.equals(newWeizhangMessage.getCode())) {
            if (isFromHistory) {
                return weizhangMessage;
            } else if (weizhangHistoryService != null) {
                // 从历史记录中查询结果
                WeizhangMessage historyWeizhangMessage = weizhangHistoryService
                        .getHistory(weizhangMessage.getCarInfo());
                if (historyWeizhangMessage != null) {
                    return historyWeizhangMessage;
                }
            }
        }
       
        // 查询到最新结果更新到历史记录中
        if (newWeizhangMessage != null
                && WeizhangMessage.SUCCESS_CODE.equals(newWeizhangMessage.getCode())){
            // 查询成功，记录到历史记录中
            weizhangHistoryService.appendHistory(newWeizhangMessage);
        }
        
        // 返回结果
        return newWeizhangMessage;
    }
    
    /**
     * 从官网服务器上查询违章记录
     * 
     * @param carInfo
     * @return
     */
    public abstract WeizhangMessage searchWeizhangMessage(CarInfo carInfo, 
            boolean isFromHistory, VerificationCode verificationCode);
}
