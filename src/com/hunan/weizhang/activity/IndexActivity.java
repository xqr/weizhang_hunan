package com.hunan.weizhang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hunan.weizhang.adapter.HistoryListAdapter;
import com.hunan.weizhang.api.client.WeatherApiClient;
import com.hunan.weizhang.model.WeatherInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.sprzny.hunan.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class IndexActivity extends Activity {
    
    private String defaultCityName = "长沙"; 
    private String cityName = null;
    private TextView mDingweiCity;
    
    private TextView mWeatherTmp;
    private ImageView mWeatherIcon;
    private TextView mWeatherWenzi;
    private TextView mWeatherNow;
    private TextView mXcIndex;
    private TextView mXcDetails;
    
    // 网格Icons
    private GridView mGview;
    private SimpleAdapter mGviewAdapter;
    
    private Intent intent;  
    
    private ListView mHistoryView;
    private HistoryListAdapter mHistoryAdapter;
    private WeizhangHistoryService weizhangHistoryService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        
        intent = new Intent();
        weizhangHistoryService = new WeizhangHistoryService(IndexActivity.this);
        
        mGview = (GridView) findViewById(R.id.icon_gview);
        mHistoryView = (ListView) findViewById(R.id.history_list);
        
        mDingweiCity = (TextView) findViewById(R.id.dingwei_city);
        initWeather();
        initIcons();
        
        // 查询历史
        new SearchHistoryTask().execute();
        
        // TODO 优先定位城市
        
        // TODO 查询天气
        new WeatherTask().execute(cityName);
    }
    
   /**
    * 初始化天气模块控件
    */
   private void initWeather() {
       mWeatherTmp = (TextView) findViewById(R.id.weather_tmp);
       mWeatherIcon = (ImageView) findViewById(R.id.weather_icon);
       mWeatherWenzi = (TextView) findViewById(R.id.weather_wenzi);
       mWeatherNow = (TextView) findViewById(R.id.weather_now);
       mXcIndex = (TextView) findViewById(R.id.xc_index);
       mXcDetails = (TextView) findViewById(R.id.xc_details);
   }
    
   public class  WeatherTask extends AsyncTask<String, WeatherInfo, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String cityname = params[0];
            if (cityname == null || cityname.equals("")) {
                cityname = defaultCityName;
            }
            WeatherInfo weather = WeatherApiClient.recentweathers(cityname);
            if (weather == null) {
                return false;
            }
            
            publishProgress(weather);
            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(WeatherInfo... values) {
            WeatherInfo weather = values[0];
            if (weather == null) {
                return;
            }
            mWeatherTmp.setText(weather.getLowtemp() + "~" + weather.getHightemp());
            mWeatherWenzi.setText(weather.getType());
            mWeatherIcon.setImageResource(weather.getWeatherIconResourceId());
            mWeatherNow.setText(weather.getAqi() + " 空气" + weather.getPmLevel().getText());
            mWeatherNow.setBackgroundColor(getResources().getColor(weather.getPmLevel().getColor()));

            mXcIndex.setText("洗车指数：" + weather.getXcIndex());
            mXcDetails.setText(weather.getXcDetails());
        }
   }
   
    /**
     * 查询历史记录
     */
    public class SearchHistoryTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // 查询违章历史
            List<WeizhangMessage> weizhangMessageList = weizhangHistoryService.getHistory();
            if (mHistoryAdapter == null) {
                mHistoryAdapter = new HistoryListAdapter(IndexActivity.this, weizhangMessageList);
            }
            
            publishProgress();
            
            return true;
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            
            mHistoryView.setAdapter(mHistoryAdapter);
        }
    }
    
    
    /**
     * 首页图标网格
     */
    private void initIcons() {
        //新建适配器
        String [] from ={"image","text"};
        int [] to = {R.id.image, R.id.text};
        mGviewAdapter = new SimpleAdapter(this, getIconData(), R.layout.csy_griditem_index, from, to);
        //配置适配器
        mGview.setAdapter(mGviewAdapter);
        mGview.setOnItemClickListener(new IconItemClickListener());
    }
    
    /**
     * icon点击，切换Activity
     */
    class  IconItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (position == 0) {
                // 违章查询
                intent.setClass(IndexActivity.this, MainActivity.class);  
                startActivity(intent);
            } else {
                // TODO 
                Toast.makeText(IndexActivity.this, "功能还未实现", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * 查询Grid Icon对象
     * 
     * @return
     */
    private List<Map<String, Object>> getIconData() {
        int[] icon = { R.drawable.icon_weizhang, R.drawable.icon_tingchechang,
                R.drawable.icon_jiayouzhan, R.drawable.icon_yinhang };
        String[] iconName = { "违章查询", "附近停车", "附近加油", "附近银行" };
        
        
        List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
