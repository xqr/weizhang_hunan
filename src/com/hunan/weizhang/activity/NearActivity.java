package com.hunan.weizhang.activity;

import java.util.List;

import com.hunan.weizhang.adapter.NearListAdapter;
import com.hunan.weizhang.api.client.LbsSearchService;
import com.hunan.weizhang.model.CustomSearchResultData;
import com.hunan.weizhang.model.LatLng;
import com.hunan.weizhang.utils.NetUtil;
import com.sprzny.quanguo.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class NearActivity extends BaseActivity {
    
    private LatLng latLng;
    private String keywords;
    
    private List<CustomSearchResultData> mNearDataList;
    private NearListAdapter mNearListAdapter = null;
    private ListView mNearListView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        
        Intent intent = this.getIntent();
        
        latLng = (LatLng) intent.getSerializableExtra("latLng");
        keywords = intent.getStringExtra("keywords");
        
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("附近" + keywords);
        
        // 返回按钮
        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 列表对象
        mNearListView =  (ListView) findViewById(R.id.list_near_line);
        
        // 检查网络情况
        if (!NetUtil.checkNet(this)) {
            Toast.makeText(NearActivity.this, "网络连接不可用", Toast.LENGTH_LONG).show();
            return;
        }
        
        // 查询附近任务
        new NearTask().execute();
    }
    
    public class  ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            CustomSearchResultData data = mNearDataList.get(position);
            if (data != null) {
                if (!startNativeBaidu(data.getLocation())) {
                    if (!startNativeGaode(data.getLocation())) {
                        Toast.makeText(NearActivity.this, "请安装百度地图或高德地图", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        
        @SuppressWarnings("deprecation")
        private boolean startNativeBaidu(LatLng destination) {
            try {            
                Intent newIntent = Intent
                        .getIntent("intent://map/direction?mode=driving&origin=" + String.format("%s,%s", latLng.getLat(), latLng.getLng())
                                +"&destination="+String.format("%s,%s", destination.getLat(), destination.getLng())
                                +"&src=com.sprzny.hunan#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                startActivity(newIntent); // 启动调用
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        
        private boolean startNativeGaode(LatLng destination) {
            try {
                double lng = destination.getLng()-0.0065;
                double lat = destination.getLat()-0.0060;
                Intent intent = new Intent("android.intent.action.VIEW",
                android.net.Uri.parse("androidamap://navi?sourceApplication=com.sprzny.hunan&lat="
                            + lat+ "&lon=" + lng +"&dev=0"));
                intent.setPackage("com.autonavi.minimap");
                startActivity(intent);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
    
    
    public class NearTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            mNearDataList = LbsSearchService.searchNearby(keywords, latLng);
            if (mNearDataList == null) {
                return true;
            }
            
            publishProgress();
            
            return true;    
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            
            if (mNearDataList ==null 
                    || mNearDataList.size() == 0) {
                return;
            }
            
            if (mNearListAdapter == null) {
                mNearListAdapter = new NearListAdapter(NearActivity.this, mNearDataList);
            }
            
            mNearListView.setAdapter(mNearListAdapter);
            mNearListView.setOnItemClickListener(new ListItemClickListener());
        }
    }
}
