package com.hunan.weizhang.api.client;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;

import com.hunan.weizhang.model.OilPriceInfo;
import com.hunan.weizhang.utils.HttpClientUtils;

public class OilPriceApiClient {
    /**
     * 查询油价
     * 
     * @param prov
     * @return
     */
    public static OilPriceInfo SearchOilPrice(String prov) {
        
        try {
            String url = "http://www.sprzny.com/hunan/youjia/" + URLEncoder.encode(prov, "UTF-8");
            
            Map<String, String> headerMap = new HashMap<String, String>();
            
            String content = HttpClientUtils.getResponse(url, headerMap);
            if (content == null || TextUtils.isEmpty(content)) {
                return null;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes != null
                    && jsonNodes.get("showapi_res_code").getIntValue() == 0) {
                jsonNodes = mapper.readValue(jsonNodes.get("showapi_res_body"), JsonNode.class);
                if (jsonNodes == null) {
                    return null;
                }
                jsonNodes = mapper.readValue(jsonNodes.get("list"), JsonNode.class);
                if (jsonNodes == null) {
                    return null;
                }
                
                for (JsonNode node : jsonNodes) {
                    return mapper.readValue(node, OilPriceInfo.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
