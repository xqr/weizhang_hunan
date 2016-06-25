package com.hunan.weizhang.api.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.hunan.weizhang.model.CustomSearchResultData;
import com.hunan.weizhang.model.LatLng;
import com.hunan.weizhang.utils.HttpClientUtils;

import android.text.TextUtils;
import android.util.Log;

public class LbsSearchService {
    
    private static final String key = "Qq1nnc3cjl83vkpTvNvOE2Rs9WIKfi7p";
    private static String apiUrl = "http://api.map.baidu.com/place/v2/search";
    
    public static List<CustomSearchResultData> list = null;
    
    public static List<CustomSearchResultData> searchNearby(String keywords, LatLng latlng) {
        try {
            StringBuilder url = new StringBuilder(apiUrl)
                    .append("?location=")
                    .append(String.format("%s,%s",
                            latlng.getLat(), latlng.getLng()))
                    .append("&query=")
                    .append(URLEncoder.encode(keywords, "UTF-8"))
                    .append("&ak=").append(key)
                    .append("&radius=5000&page_size=10&page_num=0&scope=2&output=json&filter=industry_type%3Alife%7Csort_name%3Adistance%7Csort_rule%3A1");
            
            String content = HttpClientUtils.getResponse(url.toString(), null);
            list = parseSearchResultData(content);
            return list;
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.BaiduApiService", "getDirectionRoutes()", e);
        }
        return null;
    }
    
    private static List<CustomSearchResultData> parseSearchResultData(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes == null 
                    || jsonNodes.get("status").getIntValue() != 0
                    || jsonNodes.get("results") == null) {
                return null;
            }
            
            JsonNode nodes =  mapper.readValue(jsonNodes.get("results"), JsonNode.class);
            List<CustomSearchResultData> dataList = new ArrayList<CustomSearchResultData>();
            for (JsonNode node : nodes) {
                dataList.add(mapper.readValue(node, CustomSearchResultData.class));
            }
            return dataList;
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.BaiduApiService", "parseDirectionRoutes()", e);
        }
        
        return null;
    }
}
