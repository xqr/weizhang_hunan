package com.hunan.weizhang.activity;

import com.sprzny.shanghai.R;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        // 日志加密传输
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
        
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 800);
    }
    
    class splashhandler implements Runnable {
        public void run() {
            // 销毁当前Activity，切换到主页面
            Intent intent=new Intent();  
            intent.setClass(SplashActivity.this, IndexActivity.class);  
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }
}
