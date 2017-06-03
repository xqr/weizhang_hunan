package com.hunan.weizhang.app;

import java.io.IOException;
import java.io.InputStream;
import com.hunan.weizhang.api.client.WeatherApiClient;
import android.app.Application;
import android.content.res.AssetManager;

public class AppApplication extends Application {
	private static AppApplication mAppApplication;
	
	@Override
	public void onCreate() {
		super.onCreate();
//		Context context = getApplicationContext();
		mAppApplication = this;
		
		// 初始化城市
		initCity();
	}
	
	/**
	 * 初始化城市
	 */
	private void initCity() {
        try {
            AssetManager am=getAssets();
            InputStream is=am.open("city.xml");
            WeatherApiClient.init(is);
//            am.close();
        } catch (IOException e) {
            
        }
	}
	
	/** 获取Application */
	public static AppApplication getApp() {
		return mAppApplication;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		//整体摧毁的时候调用这个方法
	}
}
