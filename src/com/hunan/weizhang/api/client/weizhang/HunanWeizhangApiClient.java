package com.hunan.weizhang.api.client.weizhang;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.utils.HttpClientUtils;
import com.hunan.weizhang.utils.MD5Utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class HunanWeizhangApiClient {
    
    public static int a = 0;
    
    /**
     * 获取验证码
     * 
     */
    public static VerificationCode getVerifCodeAction() {
        TreeMap<String, String> paramTreeMap = new TreeMap<String, String>();
        paramTreeMap.put("accessid", "39001");
        // 计算签名
        paramTreeMap.put("sign", generateSign(paramTreeMap));
        
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", generateAuthorize());
        headerMap.put("Content-Type", "application/json; charset=utf-8");
        headerMap.put("User-Agent", "Apache-HttpClient/UNAVAILABLE (java 1.4)");
        
        String content = HttpClientUtils.postResponse(
                getApiUrl("http://www.hn122122.com:9511/HnjhCjWebservice/getVerifCodeAction", paramTreeMap), 
                null, headerMap);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes != null 
                    && jsonNodes.get("code").getValueAsText().equals("1")) {
                jsonNodes = mapper.readValue(jsonNodes.get("data"), JsonNode.class);
                if (jsonNodes == null) {
                    return null;
                }
                VerificationCode code = new VerificationCode();
                code.setTpyzm(jsonNodes.get("tpyzm").getValueAsText());
                code.setToken(jsonNodes.get("token").getValueAsText());
                
                return code;
            }
        } catch (Exception e) {
            Log.e("getVerifCodeAction", "getVerifCodeAction()", e);
        }
        return null;
    }
    
    /**
     * 查询违章信息
     * 
     * @param car
     * @param code
     */
    public static WeizhangMessage toQueryVioltionByCarAction(CarInfo car, VerificationCode code) {
        TreeMap<String, String> paramTreeMap = new TreeMap<String, String>();
        paramTreeMap.put("accessid", "39001");
        paramTreeMap.put("sjhm", "");
        paramTreeMap.put("jszh", "");
        try {
            paramTreeMap.put("hphm", Base64.encodeToString(
                    car.getChepaiNo().getBytes("GBK"), Base64.NO_WRAP));
            paramTreeMap.put("hpzl", car.getHaopaiType());
            paramTreeMap.put("fdjh", car.getEngineNo());
            paramTreeMap.put("randCode", code.getRandCode());
            paramTreeMap.put("token", code.getToken());
            
            // 计算签名
            paramTreeMap.put("sign", generateSign(paramTreeMap));
            
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Authorization", generateAuthorize());
            headerMap.put("Content-Type", "application/json; charset=utf-8");
            headerMap.put("User-Agent", "Apache-HttpClient/UNAVAILABLE (java 1.4)");
            
            String content = HttpClientUtils.postResponse(
                    getApiUrl("http://www.hn122122.com:9511/HnjhCjWebservice/toQueryVioltionByCarAction", paramTreeMap), 
                    null, headerMap);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            WeizhangMessage message = mapper.readValue(content, WeizhangMessage.class);
            // 数据处理
            if (message != null && message.getCode().equals("1")) {
                message.setSearchTimestamp(new Date().getTime());
                message.setCarInfo(car);
                if (message.getData() != null) {
                    for (WeizhangInfo weizhang : message.getData()) {
                        try {
                            if (weizhang.getZt().equals("0")) {
                                message.setUntreatedCount(message.getUntreatedCount() + 1);
                                message.setTotalScores(message.getTotalScores() + Integer.parseInt(weizhang.getWfjfs()));
                                message.setTotalFkje(message.getTotalFkje() + Integer.parseInt(weizhang.getFkje()));
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return message;
        } catch (Exception e) {
            Log.e("toQueryVioltionByCarAction", "toQueryVioltionByCarAction()", e);
        }
        return null;
    }
    
    
    /**
     * 拼接url
     * 
     * @param baseUrl
     * @param params
     * @return
     */
    private static String getApiUrl(String baseUrl, Map<String, String> params) {
        StringBuffer urlBuffer = new StringBuffer(baseUrl);
        if (params.size() > 0) {
            urlBuffer.append("?");
        }
        try {
            for (String key : params.keySet()) {
                urlBuffer.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"))
                        .append("&");
            }
        } catch (Exception e) {
            
        }
        String url = urlBuffer.toString();
        if (url.endsWith("&")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    /**
     * 计算 Authorization
     * 
     * @return
     */
    private static String generateAuthorize() {
        Date now = new Date();
        long timeStamp = now.getTime() / 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        int random = new Random().nextInt(100000);
        if (a == random) {
            random = random + 1250;
        }
        a = random;
        String paramString3 = sdf.format(now) + random;
        String authorizeSign = generateAuthorizeSign("B8D60895-B6D9-48CE-AB7A-6339E18F8138",
                String.valueOf(timeStamp), paramString3);
        
        return "Basic " + Base64.encodeToString((authorizeSign + ":" + timeStamp + ":" + paramString3 + ":233").getBytes(),
                Base64.NO_WRAP);
    }
    
    /**
     * 计算Authorize签名部分
     * 
     * @param paramString1
     * @param paramString2
     * @param paramString3
     * @return
     */
    private static String generateAuthorizeSign(String paramString1, String paramString2, String paramString3)
    {
      String[] arrayOfString = new String[3];
      arrayOfString[0] = paramString1;
      arrayOfString[1] = paramString2;
      arrayOfString[2] = paramString3;
      Arrays.sort(arrayOfString);
     
      return MD5Utils.encodeByMD5(arrayOfString[0] + arrayOfString[1] +arrayOfString[2]).toLowerCase(Locale.getDefault());
    }
    
    
    /**
     * 计算签名
     * 
     * @param paramTreeMap
     * @return
     */
    private static String generateSign(TreeMap<String, String> paramTreeMap) {
        if (paramTreeMap == null || paramTreeMap.isEmpty()) {
            return null;
        }
        StringBuffer signStrBuffer = new StringBuffer();
        for (String key : paramTreeMap.keySet()) {
            signStrBuffer.append(key).append("_").append(paramTreeMap.get(key));
        }
        
        return Base64.encodeToString((MD5Utils.encodeByMD5(signStrBuffer.toString()).toLowerCase(Locale.getDefault()) 
                +"34adfasDSAFJJT55454fdayuiyhjJJKjjAD8907889@#$E567F890890f8sfs8fdasfas88@98800FDASFSAfdasf").getBytes(), Base64.NO_WRAP);
    }
}
