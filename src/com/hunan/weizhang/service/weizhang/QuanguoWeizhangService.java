package com.hunan.weizhang.service.weizhang;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.service.WeizhangService;
import com.hunan.weizhang.service.WzHunanService;

public class QuanguoWeizhangService extends WeizhangService {

    public QuanguoWeizhangService(WeizhangHistoryService weizhangHistoryService) {
        super(weizhangHistoryService);
    }

    @Override
    public WeizhangMessage searchWeizhangMessage(CarInfo carInfo,
            boolean isFromHistory, VerificationCode verificationCode) {
        try {
            String content = WzHunanService.queryQuanguoWeizhang(carInfo);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode == null) {
                return null;
            }
            
            WeizhangMessage weizhangMessage = new WeizhangMessage();
            // 基本信息
            weizhangMessage.setCode(WeizhangMessage.SUCCESS_CODE);
            weizhangMessage.setCarInfo(carInfo);
            weizhangMessage.setSearchTimestamp(new Date().getTime());
            
            int status = jsonNode.get("status").getIntValue();
            if (status == 2000) { // 正常，无违章记录
                return weizhangMessage;
            } else if (status == 5000 || status == 5001) {
                // 交管局信息忙
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("交管局系统连线忙碌中，请稍后重试");
                return weizhangMessage;
            } else if (status == 5008) {
                // 信息有误
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("输入的车辆信息有误，请查证后重新输入");
                return weizhangMessage;
            } else if (status != 2001) {
                // 交管局信息忙
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage("交管局系统连线忙碌中，请稍后重试");
                return weizhangMessage;
            }
            
            // 违章汇总信息
            weizhangMessage.setUntreatedCount(jsonNode.get("count").getIntValue());
            weizhangMessage.setTotalScores(jsonNode.get("total_score").getIntValue());
            weizhangMessage.setTotalFkje(jsonNode.get("total_money").getIntValue());
            
            // 违章详情信息
            // {historys":[{"id":21842553,"car_id":10909443,"status":"N","fen":0,"occur_date":"2016-07-22 13:55:00","occur_area":"北京市大兴区旧忠桥北,北向南","city_id":0,"province_id":14,"code":"70064","info":"未按尾号限制通行的","money":100},{"id":21842563,"car_id":10909443,"status":"N","fen":3,"occur_date":"2016-04-07 17:14:00","occur_area":"北京市大兴区德贤路旺兴湖桥南过街天桥,北向南","city_id":0,"province_id":14,"code":"1352A","info":"驾驶中型以上载客载货汽车、危险物品运输车辆以外的其他机动车行驶超过规定时速10%未达20%的","money":200}]}
            JsonNode resultJsonNodeArray = mapper.readValue(jsonNode.get("historys"), JsonNode.class);
            List<WeizhangInfo> weizhangList = new ArrayList<WeizhangInfo>();
            if (resultJsonNodeArray != null) {
                for (JsonNode resultJsonNode : resultJsonNodeArray) {
                    WeizhangInfo weizhangInfo = new WeizhangInfo(resultJsonNode.get("occur_date").getTextValue(),
                            resultJsonNode.get("occur_area").getTextValue(), 
                            resultJsonNode.get("info").getTextValue(), 
                            String.valueOf(resultJsonNode.get("fen").getIntValue()), 
                            String.valueOf(resultJsonNode.get("money").getIntValue()));
                    
                    // 设置处理状态
                    weizhangInfo.setZt("0");
                    
                    weizhangList.add(weizhangInfo);
                }
            }
            weizhangMessage.setData(weizhangList);
            
            return weizhangMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
