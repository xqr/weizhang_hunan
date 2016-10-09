package com.hunan.weizhang.api.client;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;

import com.hunan.weizhang.model.WeatherInfo;
import com.hunan.weizhang.utils.HttpClientUtils;

public class WeatherApiClient {
    
    private static String apiKey = "e656c1cb433764b3fdc1df7310c64254";
    
    /**
     * 查询天气情况
     * 
     * @param cityname
     * @return
     */
    public static WeatherInfo recentweathers(String cityname) {
        
        String url = "http://apis.baidu.com/apistore/weatherservice/recentweathers?cityname=" + cityname;
        
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("apiKey", apiKey);
        
        String content = HttpClientUtils.getResponse(url, headerMap);
        if (content == null || TextUtils.isEmpty(content)) {
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes != null
                    && jsonNodes.get("errNum").getIntValue() == 0) {
                jsonNodes = mapper.readValue(jsonNodes.get("retData"), JsonNode.class);
                if (jsonNodes == null) {
                    return null;
                }
                jsonNodes = mapper.readValue(jsonNodes.get("today"), JsonNode.class);
                if (jsonNodes == null) {
                    return null;
                }
                WeatherInfo weather = new WeatherInfo();
                try {
                    // 部分城市接口返回null
                    weather.setAqi(Integer.parseInt(jsonNodes.get("aqi").getTextValue()));
                } catch (Exception e) {
                    // 忽略异常
                }
                weather.setCurTemp(jsonNodes.get("curTemp").getTextValue());
                weather.setHightemp(jsonNodes.get("hightemp").getTextValue());
                weather.setLowtemp(jsonNodes.get("lowtemp").getTextValue());
                weather.setType(jsonNodes.get("type").getTextValue());
                
                jsonNodes = mapper.readValue(jsonNodes.get("index"), JsonNode.class);
                for (JsonNode node : jsonNodes) {
                    if (node != null 
                            && node.get("code").getTextValue().equals("xc")) {
                        weather.setXcDetails(node.get("details").getTextValue());
                        weather.setXcIndex(node.get("index").getTextValue());
                        break;
                    }
                }
                return weather;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
