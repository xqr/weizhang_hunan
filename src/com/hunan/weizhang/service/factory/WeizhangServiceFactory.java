package com.hunan.weizhang.service.factory;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WeizhangService;
import com.hunan.weizhang.service.weizhang.BeijingWeizhangService;
import com.hunan.weizhang.service.weizhang.HubeiWeizhangService;
import com.hunan.weizhang.service.weizhang.HunanWeizhangService;
import com.hunan.weizhang.service.weizhang.QuanguoWeizhangService;
import com.hunan.weizhang.service.weizhang.ShanghaiWeizhangService;

public class WeizhangServiceFactory {

    /**
     * 获取可用的违章查询服务
     * 
     * @param carInfo
     * @param weizhangHistoryService
     * @return
     */
    public static WeizhangService getInstance(CarInfo carInfo, 
            WeizhangHistoryService weizhangHistoryService) {
        // TODO  选择合适的违章查询服务
        String shortName = carInfo.getChepaiNo().substring(0, 1);
        switch (shortName) {
        case "湘":
            return new HunanWeizhangService(weizhangHistoryService);
        case "沪":
            return new ShanghaiWeizhangService(weizhangHistoryService);
//        case "鄂":
//            return new HubeiWeizhangService(weizhangHistoryService);
//        case "京":
//            return new BeijingWeizhangService(weizhangHistoryService);
        default:
            return new QuanguoWeizhangService(weizhangHistoryService);
        }
    }
}
