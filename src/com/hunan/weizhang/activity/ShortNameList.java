package com.hunan.weizhang.activity;

import com.hunan.weizhang.model.ChepaiShortName;
import com.sprzny.hubei.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ShortNameList extends Activity {

    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.csy_activity_shortname);
        
        //标题
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("选择车牌所在地");
        
        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });     
        
        //省份简称列表
        gv = (GridView) findViewById(R.id.gv_1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.csy_listitem_shortname, ChepaiShortName.allShortName);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String txt = adapter.getItem(position);
                if(txt.length()>0){
                    // 选择之后再打开一个 显示城市的 Activity；
                    Intent intent = new Intent();
                    intent.putExtra("short_name", txt);
                    setResult(0, intent);
                    finish();
                }
            }
        });

    }
}