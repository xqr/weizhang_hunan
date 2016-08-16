package com.hunan.weizhang.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import com.hunan.weizhang.model.CarInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

public class WeizhangHistoryService {
    private Context context;
    private int maxCount = 6;
    
    private static List<WeizhangMessage> currentWeizhangMessage = null;
    
    public WeizhangHistoryService(Context context) {
        this.context = context;
    }
    
    public WeizhangHistoryService(Context context, int maxCount) {
        this.context = context;
        this.maxCount = maxCount;
    }
    
    public void saveHistory(List<WeizhangMessage> historyList) {
        currentWeizhangMessage = historyList;
        
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, historyList);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        SharedPreferences sp =context.getSharedPreferences("weizhang_history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history", str.toString());
        editor.commit();
    }
    
    public void appendHistory(WeizhangMessage weizhangMessage) {
        List<WeizhangMessage> list = getHistory();
        if (list == null || list.size() == 0) {
            list = new ArrayList<WeizhangMessage>();
            list.add(weizhangMessage);
            saveHistory(list);
            return;
        }
        
        List<WeizhangMessage> historyList = new ArrayList<WeizhangMessage>(maxCount);
        historyList.add(weizhangMessage);
        
        int count = 1;
        for (WeizhangMessage history : list) {
            CarInfo oldCar = history.getCarInfo();
            CarInfo car = weizhangMessage.getCarInfo();
            
            if (!(oldCar.getChepaiNo().equals(car.getChepaiNo()))
                    && count < maxCount) {
                historyList.add(history);
            }
            count++;
        }
        
        saveHistory(historyList);
    }
    
    public  WeizhangMessage getHistory(CarInfo car) {
        List<WeizhangMessage> list = getHistory();
        if (list == null || list.size() == 0) {
            return null;
        }
        for (WeizhangMessage weizhangMessage : list) {
            CarInfo  oldCar = weizhangMessage.getCarInfo();
            if (oldCar.getChepaiNo().equals(car.getChepaiNo()) 
                    && oldCar.getEngineNo().equals(car.getEngineNo())) {
                return weizhangMessage;
            }
        }
        return null;
    }
    
    public List<WeizhangMessage> getHistory() {
        if (currentWeizhangMessage != null) {
            return currentWeizhangMessage;
        }
        
        SharedPreferences sp =context.getSharedPreferences("weizhang_history_strs", 0);
        String content = sp.getString("history", "");
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null) {
                List<WeizhangMessage> list = new ArrayList<WeizhangMessage>();
                for (JsonNode node : jsonNode) {
                    list.add(mapper.readValue(node, WeizhangMessage.class));
                }
                currentWeizhangMessage = list;
                return list;
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        return null;
    }
    
    
    public void deleteHistory() {
        SharedPreferences sp =context.getSharedPreferences("weizhang_history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history", "");
        editor.commit();
        
        currentWeizhangMessage = new ArrayList<WeizhangMessage>();
    }
}
