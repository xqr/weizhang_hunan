package com.hunan.weizhang.api.client.weizhang;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.WeizhangInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.utils.HttpClientUtils;
import com.hunan.weizhang.utils.MD5Utils;

/**
 * 湖南违章信息查询
 * 1、使用智慧长沙api，仅支持湘牌
 */
public class ZhihuiChangshaWeizhangApiClient {
private static List<ZhihuiChangshaWeizhangYParams> userList = new ArrayList<>();
    
    static {
        userList.add(new ZhihuiChangshaWeizhangYParams("3557846382771042517", "720221254205947530"));
        userList.add(new ZhihuiChangshaWeizhangYParams("3838334318196234639", "970932375254931980"));
    }
    
    /**
     * 随机取用户信息
     * 
     * @return
     */
    private static ZhihuiChangshaWeizhangYParams getRandomYParams() {
        if (userList.size() == 0) {
            return null;
        }
        int random = new Random().nextInt(userList.size());
        return userList.get(random);
    }
    
    /**
     * 固定参数值
     * 
     * @return
     */
    private static Map<String, Object> getBaseParams() {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        
        paramsMap.put("limit", "999");
        paramsMap.put("appversion", "2.30");
        paramsMap.put("y0105", "ANDROID");
        paramsMap.put("osversion", "android4.4.2");
        paramsMap.put("localcity", "长沙市");
        paramsMap.put("localcounty", "芙蓉区");
        paramsMap.put("localprovince", "湖南省");
        paramsMap.put("connecttype", "WiFi");
        paramsMap.put("setupsource", "应用酷");
        
        return paramsMap;
    }
    
    /**
     * 查询违章信息
     * 
     * @param car
     * @return
     */
    public static WeizhangMessage toQueryVioltionByCarAction(CarInfo car) {
        String url = "http://www.zhcsapp.cn:8686/zhcsserver/jtwf_info.action";
        
        Map<String, Object> paramsMap = getBaseParams();
        // 发动机后5位
        String engineNo = car.getEngineNo();
        if (engineNo.length() > 5) {
            engineNo.substring(engineNo.length() - 5, engineNo.length());
        }
        paramsMap.put("fdjhm", engineNo);
        // 车牌不带第一个字母
        paramsMap.put("card", car.getChepaiNo().substring(1));
        paramsMap.put("cardtype", car.getHaopaiType());
        
        // 需要计算的参数
        ZhihuiChangshaWeizhangYParams yParams = getRandomYParams();
        paramsMap.put("y0103", yParams.getY0103());
        paramsMap.put("y0102", yParams.getY0102());
        
        long timestamp = new Date().getTime();
        paramsMap.put("timestamp", String.valueOf(timestamp));
        paramsMap.put("key", getKey(String.valueOf(timestamp), yParams.getY0102()));
        
        // 模拟头部
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headerMap.put("User-Agent", "Apache-HttpClient/UNAVAILABLE (java 1.4)");
        
        try {
            String content = HttpClientUtils.postResponse(url, paramsMap, headerMap);
            // 判断返回结果为空情况
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode == null) {
                return null;
            }
            
            WeizhangMessage weizhangMessage = new WeizhangMessage();
            
            if (jsonNode.get("success") != null) {
                // 查询信息有错
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage(jsonNode.get("msg").getTextValue());
                return weizhangMessage;
            }
            
            weizhangMessage.setCode(WeizhangMessage.SUCCESS_CODE); // 响应码
            weizhangMessage.setMessage(null); // 响应信息
            
            // 查询成功，解析查询结果
            List<WeizhangInfo> weizhangList = new ArrayList<WeizhangInfo>();
            JsonNode resultJsonNodeArray = mapper.readValue(jsonNode.get("datas"), JsonNode.class);
            int totalFkje = 0;
            int totalWfjfs = 0;
            if (resultJsonNodeArray != null) {
                for (JsonNode resultJsonNode : resultJsonNodeArray) {
                    int fkje = Integer.parseInt(resultJsonNode.get("fkje").getTextValue());
                    int wfjfs = Integer.parseInt(resultJsonNode.get("wfjf").getTextValue());
                    totalFkje = totalFkje + fkje;
                    totalWfjfs = totalWfjfs + wfjfs;
                    WeizhangInfo weizhangInfo = new WeizhangInfo(resultJsonNode.get("wfsj").getTextValue(),
                            resultJsonNode.get("wfdz").getTextValue(), 
                            resultJsonNode.get("wfxw").getTextValue(), 
                            String.valueOf(wfjfs), 
                            String.valueOf(fkje));
                    
                    weizhangInfo.setZt("0");
                    weizhangList.add(weizhangInfo);
                }
            }
            weizhangMessage.setData(weizhangList);
            weizhangMessage.setSearchTimestamp(new Date().getTime());
            weizhangMessage.setTotalFkje(totalFkje);
            weizhangMessage.setTotalScores(totalWfjfs);
            weizhangMessage.setUntreatedCount(weizhangList.size());
            weizhangMessage.setCarInfo(car);
            
            return weizhangMessage;
            
        } catch (Exception e) {
            // TODO 
        }
        return null;
    }
    
    /**
     * 生成签名key
     * 
     * @param time
     * @param channelId
     * @return
     */
    private static String getKey(String time, String channelId) {
        String key = "zhcscommp1a2s3s4";
        String keyA = MD5Utils.encodeByMD5(time + channelId + key).toLowerCase(Locale.CHINA);
        String keyB = "45be6bf7eec914c9992f5bfdb3f8c2bd";
        return '2' + MD5Utils.encodeByMD5(keyA + keyB).toLowerCase(Locale.CHINA);
    }
}
