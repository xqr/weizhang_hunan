package com.hunan.weizhang.api.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.text.TextUtils;

import com.hunan.weizhang.model.WeatherInfo;
import com.hunan.weizhang.utils.HttpClientUtils;

public class WeatherApiClient {
    
    /**
     * 查询天气情况
     * 
     * @param cityname
     * @return
     */
    public static WeatherInfo recentweathers(String cityname) {
        String url = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + getCityId(cityname);
        
        String content = HttpClientUtils.getResponse(url, null);
        if (content == null || TextUtils.isEmpty(content)) {
            return null;
        }
        // 开始解析
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        try {
            //得到DocumentBuilder对象
            DocumentBuilder builder=factory.newDocumentBuilder();
            //得到代表整个xml的Document对象
            Document document=builder.parse(new InputSource(new ByteArrayInputStream(content.getBytes("utf-8"))));
            //得到 "根节点" 
            Element root=document.getDocumentElement();
            // PM和温度
            NodeList environment = root.getElementsByTagName("environment");
            if (environment == null) {
                return null;
            }
            WeatherInfo weather = new WeatherInfo();
            weather.setAqi(Integer.parseInt(((Element)environment.item(0)).getElementsByTagName("aqi").item(0).getTextContent()));
            weather.setCurTemp(root.getElementsByTagName("wendu").item(0).getTextContent());
           
            // 天气
            Element weathers = (Element)root.getElementsByTagName("weather").item(0);    
            weather.setHightemp(weathers.getElementsByTagName("high").item(0).getTextContent());
            weather.setLowtemp(weathers.getElementsByTagName("low").item(0).getTextContent());
            weather.setType(((Element)weathers.getElementsByTagName("day").item(0)).getElementsByTagName("type").item(0).getTextContent());
            
            // 替换掉温度中的中文
            weather.setHightemp(weather.getHightemp().replace("高温 ", ""));
            weather.setLowtemp(weather.getLowtemp().replace("低温 ", ""));
            
            NodeList zhishus = root.getElementsByTagName("zhishu");
            for (int i = 0; i < zhishus.getLength(); i++) {
                Element zhishu = (Element) zhishus.item(i);
                String name = zhishu.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals("洗车指数")) {
                    weather.setXcDetails(zhishu.getElementsByTagName("detail").item(0).getTextContent());
                    weather.setXcIndex(zhishu.getElementsByTagName("value").item(0).getTextContent());
                    break;
                }
            }
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 根据城市名称查询城市ID
     * 
     * @param cityName
     * @return
     */
    private static String getCityId(String cityName) {
        if (cityName == null 
                || cityNamAndId == null 
                || !cityNamAndId.containsKey(cityName)) {
            return  "101020100";
        }
        return cityNamAndId.get(cityName);
    }
    
    private static Map<String, String> cityNamAndId = null;
    
    /**
     * 初始化
     * 
     * @param is
     */
    public static Map<String, String> init(InputStream stream) {
        if (cityNamAndId != null) {
            return cityNamAndId;
        }
        
        cityNamAndId = new HashMap<String, String>();
        
        // 开始解析
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        try {
            //得到DocumentBuilder对象
            DocumentBuilder builder=factory.newDocumentBuilder();
            //得到代表整个xml的Document对象
            Document document=builder.parse(stream);
            //得到 "根节点" 
            Element root=document.getDocumentElement();
            //获取根节点的所有items的节点
            NodeList items=root.getElementsByTagName("item");  
            //遍历所有节点
            for(int i=0;i<items.getLength();i++)
            {
                Element item=(Element)items.item(i);
                
                String key = item.getElementsByTagName("citynm").item(0).getTextContent();
                String value = item.getElementsByTagName("cityid").item(0).getTextContent();
                cityNamAndId.put(key, value);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }
        }
        return cityNamAndId;
    }
}
