package com.hunan.weizhang.service.weizhang;

import com.hunan.weizhang.api.client.weizhang.HubeiWeizhangApiClient;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WeizhangService;

public class HubeiWeizhangService extends WeizhangService {
    
    public HubeiWeizhangService(WeizhangHistoryService weizhangHistoryService) {
        super(weizhangHistoryService);
    }

    @Override
    public WeizhangMessage searchWeizhangMessage(CarInfo carInfo, boolean isFromHistory) {
        if (carInfo == null) {
            return null;
        }
        
        WeizhangMessage weizhangMessage = new WeizhangMessage();
        
        if (!isFromHistory 
                && carInfo.getChepaiNo().startsWith("鄂")) {
            int errorCode = HubeiWeizhangApiClient.veh(carInfo);
            if (errorCode == 0) {
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("车辆信息输入有误");
            } else if (errorCode == 2) {
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("交管局系统连线忙碌中，请稍后再试");
            }
            return weizhangMessage;
        }
        
        weizhangMessage = HubeiWeizhangApiClient.viomore(carInfo);
        if (weizhangMessage == null) {
            return null;
        }
        
        if (weizhangMessage.getMessage().equals("success")) {
            if (weizhangMessage.getData().size() == 0) {
                weizhangMessage.setMessage("恭喜, 没有查到违章记录！");
            }
        } else {
            if (carInfo.getChepaiNo().startsWith("鄂")) {
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("交管局系统连线忙碌中，请稍后再试");
            } else if (weizhangMessage.getMessage().equals("infoerror")) {
                // 车辆信息有误
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("车辆信息输入有误");
            } else {
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("交管局系统连线忙碌中，请稍后再试");
            }
        }
        
        return weizhangMessage;
    }
}
