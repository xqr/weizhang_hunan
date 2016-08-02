package com.hunan.weizhang.service.weizhang;

import com.hunan.weizhang.api.client.weizhang.ShanghaiWeizhangApiClient;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WeizhangService;

public class ShanghaiWeizhangService extends WeizhangService {

    public ShanghaiWeizhangService(WeizhangHistoryService weizhangHistoryService) {
        super(weizhangHistoryService);
    }

    @Override
    public WeizhangMessage searchWeizhangMessage(CarInfo carInfo, boolean isFromHistory) {
        return ShanghaiWeizhangApiClient.toQueryVioltionByCarAction(carInfo);
    }
}
