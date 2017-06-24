package com.hunan.weizhang.api.client.weizhang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import android.text.TextUtils;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.WeizhangInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.utils.HttpClientUtils;

public class ShanghaiWeizhangApiClient {
    /**
     * 对接上海违章查询官网
     * 
     * @param car
     * @return
     */
    public static WeizhangMessage toQueryVioltionByCarAction(CarInfo car) {
        String url = String.format("http://my.eshimin.com/weizhang/electronbill/detail?account=%s&wzStatus=0&fdjh=%s&clbj=0&hpzl=%s&_=%s",
                car.getChepaiNo(), car.getEngineNo(), car.getHaopaiType(), new Date().getTime());
        try {
            String content = HttpClientUtils.getResponse(url, null);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null 
                    && jsonNode.get("success").getBooleanValue()) {
                WeizhangMessage weizhangMessage = new WeizhangMessage();
                
                weizhangMessage.setSearchTimestamp(new Date().getTime());
                weizhangMessage.setCarInfo(car);
                
                // 违章详情
                List<WeizhangInfo> data = new ArrayList<WeizhangInfo>();
                JsonNode jsonNodeArray = mapper.readValue(jsonNode.get("data"), JsonNode.class);
                if (jsonNodeArray != null) {
                    
                    for (JsonNode node : jsonNodeArray) {
                        WeizhangInfo weizhangInfo = new WeizhangInfo();
                        
                        // 违章细节详情对象封装
                        weizhangInfo.setFkje(node.get("fkje").getTextValue()); // 罚款金额
                        try {
                            Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(node.get("wfsj").getTextValue());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            weizhangInfo.setWfsj(sdf.format(date)); // 违法时间
                        } catch (Exception e) {
                            weizhangInfo.setWfsj(node.get("wfsj").getTextValue());
                        }
                        if (node.get("xwsm") != null) {
                            weizhangInfo.setWfxw(node.get("xwsm").getTextValue()); // 违法行为
                        } else {
                            weizhangInfo.setWfxw("");
                        }
                        weizhangInfo.setWfdz(node.get("wfdz").getTextValue()); // 违法地址
                        weizhangInfo.setWfjfs(String.valueOf(node.get("kfsm").getIntValue())); // 扣分
                        weizhangInfo.setZt("0"); // 处理状态
                        
                        data.add(weizhangInfo);
                    }
                }
                
                if (data.size() > 0) {
                    // 总分和总金额
                    weizhangMessage.setTotalScores(jsonNode.get("koufenZJ").getIntValue());
                    weizhangMessage.setTotalFkje(jsonNode.get("fuanZJ").getIntValue());
                    weizhangMessage.setMessage(jsonNode.get("message").getTextValue());
                }
                
                weizhangMessage.setUntreatedCount(data.size());
                weizhangMessage.setCode(WeizhangMessage.SUCCESS_CODE);
                weizhangMessage.setData(data);
                return weizhangMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
