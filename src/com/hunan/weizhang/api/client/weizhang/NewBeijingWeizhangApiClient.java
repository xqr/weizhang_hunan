package com.hunan.weizhang.api.client.weizhang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.VerificationCode;
import com.hunan.weizhang.model.WeizhangInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.utils.HttpClientUtils;

public class NewBeijingWeizhangApiClient {
    
    /**
     * 查询违章信息
     * 车牌 + 发动机全称
     * 
     * @param carInfo
     * @return
     */
    public static WeizhangMessage viomore(CarInfo carInfo, VerificationCode code, String referer, String cookie) {
        try {
            String url = String.format("http://bjjj.bjjtgl.gov.cn/jgjapp/ui/vehicle/selectVehicle.htm?runnername=%s&fadongji=%s&yanzhengma=%s&date=%s",
                    URLEncoder.encode(carInfo.getChepaiNo(), "UTF-8"), carInfo.getEngineNo(), code.getRandCode(), getRandomDate());
            
            // 模拟头部
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json;charset=utf-8");
            headerMap.put("Referer", referer);
            headerMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            headerMap.put("Cookie", String.format("plateNO=%s; motorNO=%s; %s", carInfo.getChepaiNo().substring(1), carInfo.getEngineNo(), cookie));
            
            String content = HttpClientUtils.getResponse(url, headerMap);
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
            if (!jsonNode.get("success").getBooleanValue()) {
                // 查询失败
                weizhangMessage.setCode(WeizhangMessage.ERROR_CODE);
                weizhangMessage.setMessage(jsonNode.get("msg").getTextValue());
                return weizhangMessage;
            }
            // TODO 无违章的情况
            JsonNode resultJsonNodeArray = mapper.readValue(jsonNode.get("data").get("busIllegalRecords"), JsonNode.class);
            if (resultJsonNodeArray == null) {
                return null;
            }
            
            // 设置成功的标记
            weizhangMessage.setCode(WeizhangMessage.SUCCESS_CODE);
            weizhangMessage.setMessage(jsonNode.get("msg").getTextValue());
            
            // 查询成功，解析查询结果
            List<WeizhangInfo> weizhangList = new ArrayList<WeizhangInfo>();
            int totalFkje = 0;
            int totalWfjfs = 0;
            for(JsonNode resultJsonNode : resultJsonNodeArray) {
                WeizhangInfo weizhangInfo = new WeizhangInfo(resultJsonNode.get("illegalTime").getTextValue(),
                        resultJsonNode.get("illegalAddress").getTextValue(), 
                        resultJsonNode.get("illegalAction").getTextValue(), 
                        resultJsonNode.get("illegalPoint").getTextValue(), 
                        resultJsonNode.get("illegalFine").getTextValue());
                // 设置处理状态
                weizhangInfo.setZt("0");
                
                try{
                    totalFkje = totalFkje + Integer.parseInt(weizhangInfo.getFkje());
                    totalWfjfs = totalWfjfs + Integer.parseInt(weizhangInfo.getWfjfs());
                } catch (Exception e) {
                    
                }
                
                weizhangList.add(weizhangInfo);
            }
            
            weizhangMessage.setData(weizhangList);
            weizhangMessage.setSearchTimestamp(new Date().getTime());
            weizhangMessage.setTotalFkje(totalFkje);
            weizhangMessage.setTotalScores(totalWfjfs);
            weizhangMessage.setUntreatedCount(weizhangList.size());
            weizhangMessage.setCarInfo(carInfo);
            
            return weizhangMessage;
            
        } catch (Exception e) {
            // TODO 
            e.printStackTrace();
        }
        return null;
    }
  
    /**
     * 生成32位随机数
     * 
     * @return
     */
    private static String getUserId() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        if (uuid.length() > 32) {
            return uuid.substring(0, 32);
        } else if (uuid.length() == 32) {
            return uuid;
        }
        return "E70B97EDB0A24AC8849A600BDD29C76F";
    }
    
    /**
     * 随机生成16位数字
     * 
     * @return
     */
    private static String getRandomDate() {
        String result="0.";
        Random random = new Random();
        for(int i = 0; i< 15; i++) {
            int index = random.nextInt(10);
            result = result + index;
        }
        return result;
    }
    
    /**
     * 获取Referer
     * 
     * @return
     */
    public static String getRefererUrl() {
        return String.format("http://bjjj.bjjtgl.gov.cn/jgjapp/ui/vehicle/toVehicle.htm?userId=%s&snsId=%s", 
                getUserId(), getUserId());
    }
    
    /**
     * 获取cookieId
     * 
     * @param url
     * @return
     */
    public static String getJessionid(String url) {
        // 模拟头部
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "text/html;charset=UTF-8");
        headerMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
        headerMap.put("Upgrade-Insecure-Requests", "1");
        
        return HttpClientUtils.getResponseCookie(url, headerMap);
    }
    
    /**
     * 获取验证码
     * 
     * @return
     */
    public static VerificationCode getVerificationCode(String referer, String cookie) {
        // http://bjjj.bjjtgl.gov.cn/jgjapp/ui/driver/createCode.htm?date=0.7599093733670679
        try {
            String url = String.format("http://bjjj.bjjtgl.gov.cn/jgjapp/ui/driver/createCode.htm?date=%s", getRandomDate());
            
            // 模拟头部
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "image/jpeg");
            headerMap.put("Referer", referer);
            headerMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            headerMap.put("Cookie", cookie);
            
            HttpResponse response = HttpClientUtils.getHttpResponse(url, headerMap);
            // 判断返回结果为空情况
            if (response == null 
                    || response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            
            // 解析验证码图片流
            byte[] tpyzmByte = InputStreamTOByte(response.getEntity().getContent());
            if (tpyzmByte != null){
                VerificationCode verificationCode = new VerificationCode();
                verificationCode.setTpyzmByte(tpyzmByte);
                return verificationCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**  
     * 将InputStream转换成byte数组  
     * @param in InputStream  
     * @return byte[]  
     * @throws IOException  
     */  
    private static byte[] InputStreamTOByte(InputStream in) throws IOException{  
        int BUFFER_SIZE = 4096;  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[BUFFER_SIZE];  
        int count = -1;  
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return outStream.toByteArray();  
    } 
}
