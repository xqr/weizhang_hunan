package com.hunan.weizhang.activity;

import com.sprzny.hunan.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
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
