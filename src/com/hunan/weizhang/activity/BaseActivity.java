package com.hunan.weizhang.activity;

import com.umeng.analytics.MobclickAgent;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
