package com.hunan.weizhang.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import android.util.Base64;
import android.util.Log;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.utils.HttpClientUtils;

public class WzHunanService {
    
    public static String queryWeizhang(CarInfo car, String telephone) {
        if (telephone == null || telephone.length() == 0) {
            return null;
        }
        
        Map<String, String> data = new HashMap<String, String>();
        data.put("carno", car.getChepaiNo());
        data.put("engineno", car.getEngineNo());
        data.put("classno", car.getChejiaNo());
        data.put("mobile", telephone);
        
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, data);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("data", Base64.encodeToString(str.toString().getBytes(), Base64.NO_WRAP));
        
        return HttpClientUtils.postResponse("http://www.sprzny.com/api/idcard", params, null);
    }
    
    /**
     * 查询全国违章
     * 
     * @param car
     * @return
     */
    public static String queryQuanguoWeizhang(CarInfo car) {        
        Map<String, String> data = new HashMap<String, String>();
        data.put("hphm", car.getChepaiNo());
        data.put("engineno", car.getEngineNo());
        data.put("classno", car.getChejiaNo());
        
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, data);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("data", Base64.encodeToString(str.toString().getBytes(), Base64.NO_WRAP));
        
        return HttpClientUtils.postResponse("http://wz.cs.cn/hunan/quanguo", params, null);
    }
}
