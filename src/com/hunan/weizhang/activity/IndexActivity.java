package com.hunan.weizhang.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.hunan.weizhang.adapter.HistoryListAdapter;
import com.hunan.weizhang.api.client.WeatherApiClient;
import com.hunan.weizhang.model.LatLng;
import com.hunan.weizhang.model.WeatherInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.qrcode.QrCodeExample;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.sprzny.hunan.R;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class IndexActivity extends BaseActivity {
    // 用户位置定位
    private String defaultCityName = "长沙"; 
    private String cityName = null;
    private TextView mDingweiCity;
    private LatLng latLng = null;
    
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    
    // 天气View
    private TextView mWeatherTmp;
    private ImageView mWeatherIcon;
    private TextView mWeatherWenzi;
    private TextView mWeatherNow;
    private TextView mXcIndex;
    private TextView mXcDetails;
    private RelativeLayout mweatherLayout;
    
    // 网格Icons
    private GridView mGview;
    private SimpleAdapter mGviewAdapter;
    
    // 违章记录历史查询和展示
    private List<WeizhangMessage> weizhangMessageList;
    private ListView mHistoryView;
    private HistoryListAdapter mHistoryAdapter;
    private WeizhangHistoryService weizhangHistoryService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        
        weizhangHistoryService = new WeizhangHistoryService(IndexActivity.this);
        
        mweatherLayout = (RelativeLayout) findViewById(R.id.weather);
        mGview = (GridView) findViewById(R.id.icon_gview);
        mHistoryView = (ListView) findViewById(R.id.history_list);
        
        mDingweiCity = (TextView) findViewById(R.id.dingwei_city);
        initWeather();
        initIcons();
        
        // 查询历史
        new SearchHistoryTask().execute();
        
        // 定位城市
        if (cityName == null || cityName.length() == 0 || latLng == null) {
            mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
            mLocationClient.registerLocationListener( myListener );    //注册监听函数
            initLocation();
            mLocationClient.start();
        } else {
            // 查询天气
            mDingweiCity.setText(cityName);
            new WeatherTask().execute(cityName);
        }
//        
//        // 初始化验证码
//        try {
//            AssetManager am=getAssets();
//            InputStream is=am.open("code.txt");
//            QrCodeExample.init(is);
//        } catch (IOException e) {
//            
//        }
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
            
            mweatherLayout.setVisibility(View.VISIBLE);
        }
   }
   
    /**
     * 查询历史记录
     */
    public class SearchHistoryTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // 查询违章历史
            weizhangMessageList = weizhangHistoryService.getHistory();
            if (weizhangMessageList == null || weizhangMessageList.size() == 0) {
                return true;
            }
            
            publishProgress();
            
            return true;
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            
            if (mHistoryAdapter == null) {
                mHistoryAdapter = new HistoryListAdapter(IndexActivity.this, weizhangMessageList);
            }
            
            mHistoryView.setAdapter(mHistoryAdapter);
            mHistoryView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    
                    Intent intent = new Intent();
                    
                    WeizhangMessage weizhangMessage = weizhangMessageList.get(position);
                    if (weizhangMessage == null) {
                        intent.setClass(IndexActivity.this, MainActivity.class);  
                        startActivity(intent);
                        return;
                    }
                    
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("weizhangMessage", weizhangMessage);
                    bundle.putString("flag", "history");
                    intent.putExtras(bundle);
                    intent.setClass(IndexActivity.this, WeizhangResult.class);  
                    startActivity(intent);
                }
            });
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
            
            Intent intent = new Intent();
            
            if (position == 0) {
                // 违章查询
                intent.setClass(IndexActivity.this, MainActivity.class);  
                startActivity(intent);
            } else {
                String queryName = "停车场";
                if (position == 2) {
                    queryName = "加油站";
                } else if (position == 3) {
                    queryName ="银行";
                }
                
                if (latLng == null) {
                    Toast.makeText(IndexActivity.this, "定位失败，请稍后重试", Toast.LENGTH_LONG).show();
                    return;
                }
                
                Bundle bundle = new Bundle();
                bundle.putSerializable("latLng", latLng);
                bundle.putString("keywords", queryName);
                intent.putExtras(bundle);
                
                intent.setClass(IndexActivity.this, NearActivity.class);
                startActivity(intent); 
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
    
    /**
     * 初始化配置
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Device_Sensors);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
    
    public class MyLocationListener implements BDLocationListener {
        
        @Override
        public void onReceiveLocation(BDLocation location) {
            
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation
                    || location.getLocType() == BDLocation.TypeOffLineLocation) {
                String city = location.getCity();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (city == null || city.isEmpty()) {
                    mDingweiCity.setText(defaultCityName);
                    new WeatherTask().execute(defaultCityName);
                    return;
                }
                if (city.endsWith("市")) {
                    cityName = city.substring(0, city.length() - 1);
                } else {
                    cityName = city;
                }
                mDingweiCity.setText(cityName);
                new WeatherTask().execute(cityName);
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        if (mLocationClient != null 
                && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        super.onDestroy();
    }
}
