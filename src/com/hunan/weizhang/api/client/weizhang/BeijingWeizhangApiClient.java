package com.hunan.weizhang.api.client.weizhang;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.WeizhangInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.utils.HttpClientUtils;

public class BeijingWeizhangApiClient {

    /**
     * 查询违章信息
     * 车牌 + 发动机全称
     * 
     * @param carInfo
     * @return
     */
    public static WeizhangMessage viomore(CarInfo carInfo) {
        try {
            String url = String.format("https://web.weifachajiao.zhongchebaolian.com/inquire/mainController/getCarInfo.do?userid=%s&licenseno=%s&engineno=%s&platetype=%s",
                    getUserId(), URLEncoder.encode(carInfo.getChepaiNo(), "UTF-8"), carInfo.getEngineNo(), carInfo.getHaopaiType());
            
            // 模拟头部
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "text/html;charset=UTF-8");
            headerMap.put("Referer", "https://web.weifachajiao.zhongchebaolian.com/inquire/skip.jsp");
            headerMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            
            
            String content = HttpClientUtils.getHttpsResponse(url, headerMap);
            // 判断返回结果为空情况
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            
            Document doc = Jsoup.parse(content); 
            
            WeizhangMessage weizhangMessage = new WeizhangMessage();
            // 解析总况
            Element endorsement = doc.getElementsByClass("endorsement").first();
            parseTotal(weizhangMessage, endorsement);
            // 解析违法详情
            Elements breakInfoList = doc.getElementsByClass("break_info_list");
            parseBreakInfo(weizhangMessage, breakInfoList);
            
            weizhangMessage.setCode(WeizhangMessage.SUCCESS_CODE); // 响应码
            weizhangMessage.setMessage(null); // 响应信息
            weizhangMessage.setSearchTimestamp(new Date().getTime()); // 查询时间
            weizhangMessage.setCarInfo(carInfo); // 查询车辆信息
            
            return weizhangMessage;
        } catch (Exception e) {
            // TODO 
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 解析罚款总金额和总分
     * 
     * @param weizhangMessage
     * @param endorsement
     * @return
     */
    private static WeizhangMessage parseTotal(WeizhangMessage weizhangMessage, Element endorsement) {
        Elements tags = endorsement.getElementsByTag("span");
        weizhangMessage.setUntreatedCount(Integer.parseInt(tags.get(0).text()));
        weizhangMessage.setTotalScores(Integer.parseInt(tags.get(1).text()));
        weizhangMessage.setTotalFkje(Integer.parseInt(tags.get(2).text()));
        
        return weizhangMessage;
    }
    
    /**
     * 解析违法详情
     * 
     * @param weizhangMessage
     * @param endorsement
     * @return
     */
    private static WeizhangMessage parseBreakInfo(WeizhangMessage weizhangMessage, Elements breakInfoList) {
        List<WeizhangInfo> data = new ArrayList<WeizhangInfo>();
        
        for (Element  breakInfo : breakInfoList) {
            WeizhangInfo weizhangInfo = new WeizhangInfo(breakInfo.getElementsByTag("time").first().text(), 
                    breakInfo.getElementsByTag("address").first().text(), 
                    breakInfo.getElementsByTag("p").first().text(), 
                    breakInfo.getElementById("jf").text(), 
                    breakInfo.getElementsByTag("em").first().text());
            
            // 设置处理状态
            weizhangInfo.setZt("0");
            
            data.add(weizhangInfo);
        }
        
        weizhangMessage.setData(data);
        
        return weizhangMessage;
    }
    
    
    /**
     * 生成32位随机数
     * 
     * @return
     */
    private static String getUserId() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace('-', ' ');
        if (uuid.length() > 32) {
            return uuid.substring(0, 32);
        } else if (uuid.length() == 32) {
            return uuid;
        }
        return "E70B97EDB0A24AC8849A600BDD29C76F";
    }
}
