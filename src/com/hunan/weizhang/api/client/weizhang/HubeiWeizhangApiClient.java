package com.hunan.weizhang.api.client.weizhang;

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
import com.hunan.weizhang.utils.MD5Utils;

public class HubeiWeizhangApiClient {
    
    /**
     * 校验参数是否合法
     * 查询方式：车牌号+车架号后5位
     * 
     * @param carInfo 
     * @return 1：正常，0：参数错误；2：服务器异常
     */
    public static int veh(CarInfo carInfo) {
        String jkxlh = MD5Utils.encodeByMD5(carInfo.getChepaiNo() + "veh");
        String chejia = carInfo.getChejiaNo();
        if (chejia.length() > 5) {
            chejia = chejia.substring(chejia.length() - 5, chejia.length());
        }
        String url = String.format("http://xxcx.hbsjg.gov.cn:8090/hbjjapp/veh/veh.do?hpzl=%s&hphm=%s&clsbdh=%s&jkxlh=%s",
                carInfo.getHaopaiType(), carInfo.getChepaiNo(), chejia, jkxlh);
        
        try {
            String content = HttpClientUtils.getResponse(url, null);
            if (TextUtils.isEmpty(content)) {
                return 0;
            }
            
            return content.contains("success") ? 1 : 0;
            
        } catch (Exception e) {
            return 2;
        }
    }
    
    /**
     * 查询违章信息
     * 查询方式：车牌号
     * 
     * @param carInfo
     */
    public static WeizhangMessage viomore(CarInfo carInfo) {
        String jkxlh = MD5Utils.encodeByMD5(carInfo.getChepaiNo() + "viomore");
        String url = String.format("http://xxcx.hbsjg.gov.cn:8090/hbjjapp/veh/viomore.do?hpzl=%s&hphm=%s&jkxlh=%s",
                carInfo.getHaopaiType(), carInfo.getChepaiNo(), jkxlh);
        
        try {
            String content = HttpClientUtils.getResponse(url, null);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode == null) {
                return null;
            }
            jsonNode = mapper.readValue(jsonNode.get("response"), JsonNode.class);
            if (jsonNode == null) {
                return null;
            }
            
            WeizhangMessage weizhangMessage = new WeizhangMessage();
            JsonNode errorJsonNode = mapper.readValue(jsonNode.get("error"), JsonNode.class);
            weizhangMessage.setCode(String.valueOf(errorJsonNode.get("code").getIntValue())); // 响应码
            weizhangMessage.setMessage(errorJsonNode.get("message").getTextValue()); // 响应信息
            
            if (!WeizhangMessage.SUCCESS_CODE.equals(weizhangMessage.getCode())) {
                return weizhangMessage;
            }
            
            // 查询成功，解析查询结果
            List<WeizhangInfo> weizhangList = new ArrayList<WeizhangInfo>();
            JsonNode resultJsonNodeArray = mapper.readValue(jsonNode.get("result"), JsonNode.class);
            int totalFkje = 0;
            int totalWfjfs = 0;
            if (resultJsonNodeArray != null) {
                for (JsonNode resultJsonNode : resultJsonNodeArray) {
                    int fkje = resultJsonNode.get("FKJE").getIntValue();
                    int wfjfs = resultJsonNode.get("WFJFS").getIntValue();
                    totalFkje = totalFkje + fkje;
                    totalWfjfs = totalWfjfs + wfjfs;
                    WeizhangInfo weizhangInfo = new WeizhangInfo(resultJsonNode.get("WFSJ").getTextValue(),
                            resultJsonNode.get("WFDZ").getTextValue(), 
                            resultJsonNode.get("WFXW").getTextValue(), 
                            String.valueOf(wfjfs), 
                            String.valueOf(fkje));
                    // 设置处理状态
                    weizhangInfo.setZt("0");
                    
                    weizhangList.add(weizhangInfo);
                }
            }
            weizhangMessage.setData(weizhangList);
            weizhangMessage.setSearchTimestamp(new Date().getTime());
            weizhangMessage.setTotalFkje(totalFkje);
            weizhangMessage.setTotalScores(totalWfjfs);
            weizhangMessage.setUntreatedCount(weizhangList.size());
            weizhangMessage.setCarInfo(carInfo);
            
            return weizhangMessage;
            
        } catch (Exception e) {
            
        }
        return null;
    }
}
