package com.hunan.weizhang.service.weizhang;

import com.hunan.weizhang.api.client.weizhang.BeijingWeizhangApiClient;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WeizhangService;

public class BeijingWeizhangService extends WeizhangService {

    public BeijingWeizhangService(WeizhangHistoryService weizhangHistoryService) {
        super(weizhangHistoryService);
    }

    @Override
    public WeizhangMessage searchWeizhangMessage(CarInfo carInfo,
            boolean isFromHistory, VerificationCode verificationCode) {
        return BeijingWeizhangApiClient.viomore(carInfo);
    }
}
