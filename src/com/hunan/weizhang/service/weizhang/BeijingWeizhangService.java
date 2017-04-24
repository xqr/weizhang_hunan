package com.hunan.weizhang.service.weizhang;

import com.hunan.weizhang.api.client.weizhang.NewBeijingWeizhangApiClient;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.qrcode.BeijingQrCodeExample;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WeizhangService;

public class BeijingWeizhangService extends WeizhangService {

    public BeijingWeizhangService(WeizhangHistoryService weizhangHistoryService) {
        super(weizhangHistoryService);
    }

    @Override
    public WeizhangMessage searchWeizhangMessage(CarInfo carInfo,
            boolean isFromHistory, VerificationCode verificationCode) {
        
        String referUrl = NewBeijingWeizhangApiClient.getRefererUrl();
        String cookie = NewBeijingWeizhangApiClient.getJessionid(referUrl);
        verificationCode = NewBeijingWeizhangApiClient.getVerificationCode(referUrl, cookie);
        if (verificationCode == null || cookie == null) {
            return null;
        }
        
        // 二维码识别
        String randomCode = BeijingQrCodeExample.qrCode(verificationCode.getTpyzmByte());
        if (randomCode == null) {
            return null;
        }
        verificationCode.setRandCode(randomCode);
        
        return NewBeijingWeizhangApiClient.viomore(carInfo, verificationCode, referUrl, cookie);
    }
}
