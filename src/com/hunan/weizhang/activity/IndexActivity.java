package com.hunan.weizhang.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.hunan.weizhang.adapter.HistoryListAdapter;
import com.hunan.weizhang.api.client.OilPriceApiClient;
import com.hunan.weizhang.api.client.WeatherApiClient;
import com.hunan.weizhang.model.LatLng;
import com.hunan.weizhang.model.OilPriceInfo;
import com.hunan.weizhang.model.WeatherInfo;
import com.hunan.weizhang.model.WeizhangMessage;
import com.hunan.weizhang.service.WeizhangHistoryService;
import com.hunan.weizhang.utils.LocationHelper;
import com.hunan.weizhang.utils.LocationUtils;
import com.hunan.weizhang.utils.NetUtil;
import com.sprzny.hubei.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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

public class IndexActivity extends BaseActivity implements LocationHelper {
    // 用户位置定位
    private String defaultCityName = "武汉"; 
    private String provName = "湖北";
    private String cityName = null;
    private TextView mDingweiCity;
    private LatLng latLng = null;
    
    // 定位类
    public LocationUtils mLocationUtils;
    
    // 天气View
    private TextView mWeatherTmp;
    private ImageView mWeatherIcon;
    private TextView mWeatherWenzi;
    private TextView mWeatherNow;
    private TextView mXcIndex;
    private RelativeLayout mweatherLayout;
    
    // 油价
    private TextView mOicPrices;
    
    // 日期
    private TextView mXcWeek;
    private TextView mXcDate;
    
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
        mOicPrices = (TextView) findViewById(R.id.oic_prices);
        initWeather();
        initIcons();
        initDate();
        
        // 查询历史
        new SearchHistoryTask().execute();
        
        // 定位城市
        if (cityName == null 
                || cityName.length() == 0 
                || latLng == null
                || provName == null 
                || provName.length() == 0) {
            mLocationUtils = new LocationUtils(this);
            mLocationUtils.initLocationListener(this);
        } else {
            showCityWeatherAndOilPrice(cityName, provName);
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
   }
   
   /**
    * 初始化日期
    */
   private void initDate() {
       mXcWeek = (TextView) findViewById(R.id.xc_week);
       mXcDate = (TextView) findViewById(R.id.xc_date);
       
       Date date=new Date(); 
       Calendar c=Calendar.getInstance(); 
       c.setTime(date); 
       //今天是这个星期的第几天 
       int week=c.get(Calendar.DAY_OF_WEEK); 
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
       mXcDate.setText(sdf.format(date));
       try {
           mXcWeek.setText(weeks[week-1]);
       } catch (Exception e) {
           
       }
   }
   
   private String[] weeks = new String[] {
           "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
   };
   
   public class  OilPriceTask extends AsyncTask<String, OilPriceInfo, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String prov = params[0];
            if (prov == null || TextUtils.isEmpty(prov)) {
                return false;
            }
            
            OilPriceInfo oilPrice = OilPriceApiClient.SearchOilPrice(prov);
            if (oilPrice == null) {
                return false;
            }
            
            publishProgress(oilPrice);
            
            return true;
        }
        
        @Override
        protected void onProgressUpdate(OilPriceInfo... values) {
            OilPriceInfo oilPrice = values[0];
            
            mOicPrices.setText(Html.fromHtml(oilPrice.getShowText()));
        }
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
     * 切换到违章Activity
     * 
     * @param v
     */
    public void weizhangClick(View v){
        Intent intent = new Intent();
        // 违章查询
        intent.setClass(IndexActivity.this, MainActivity.class);  
        startActivity(intent);
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
                weizhangClick(view);
            } else {
                Intent intent = new Intent();
                String queryName = "停车场";
                if (position == 2) {
                    queryName = "加油站";
                } else if (position == 3) {
                    queryName ="银行";
                }
                
                if (latLng == null) {
                    // 再次尝试定位
                    mLocationUtils.initLocationListener(IndexActivity.this);
                    Toast.makeText(IndexActivity.this, "请确保网络开启状态，正在定位中……", Toast.LENGTH_LONG).show();
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
        int[] icon = { R.drawable.icon_weizhang_green, R.drawable.icon_tingchechang_color,
                R.drawable.icon_jiayouzhan_color, R.drawable.icon_yinhang };
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
    
    @Override
    public void updateLocation(BDLocation location) {
        if (location.getLocType() == BDLocation.TypeGpsLocation
                || location.getLocType() == BDLocation.TypeNetWorkLocation
                || location.getLocType() == BDLocation.TypeOffLineLocation) {
            String city = location.getCity();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (city == null || city.isEmpty()) {
                showCityWeatherAndOilPrice(defaultCityName, provName);
                return;
            }
            
            if (city.endsWith("市")) {
                cityName = city.substring(0, city.length() - 1);
            } else {
                cityName = city;
            }
            String prov = location.getProvince();
            if (prov != null && !prov.isEmpty()) {
                if (prov.endsWith("省") || prov.endsWith("市")) {
                    provName = prov.substring(0, prov.length() - 1);
                } else {
                    provName = prov;
                }
            }
            // 定位成功，停止定位
            if (latLng != null) {
                mLocationUtils.removeLocationListener();
            }
            showCityWeatherAndOilPrice(cityName, provName);
        }
    }
    
    /**
     * 定位成功后，显示与定位相关信息
     * 
     * @param cityName
     * @param provName
     */
    private void showCityWeatherAndOilPrice(String cityName, String provName) {
        mDingweiCity.setText(cityName);
        // 检查网络情况
        if (!NetUtil.checkNet(this)) {
            Toast.makeText(IndexActivity.this, "网络连接不可用", Toast.LENGTH_LONG).show();
            return;
        }
        new WeatherTask().execute(cityName);
        new OilPriceTask().execute(provName);
    }
    
    @Override
    protected void onDestroy() {
        if (mLocationUtils != null ) {
            mLocationUtils.removeLocationListener();
        }
        super.onDestroy();
    }
}
