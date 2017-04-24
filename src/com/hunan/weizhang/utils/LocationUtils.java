package com.hunan.weizhang.utils;

import java.util.Date;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.content.Context;

/**
 * 公共的位置定位类
 * 
 * @author Administrator
 *
 */
public class LocationUtils {
    
    private static BDLocation currentLocation = null;
    private static long lastTimestamp = 0;
    
    private LocationClient mLocationClient = null;
    private LocationHelper pInterface;
    
    public  LocationUtils (LocationHelper pInterface) {
        this.pInterface = pInterface;
    }
    
    /**
     * 初始化监控
     * 
     * @param pContext
     * @return
     */
    public boolean initLocationListener(Context pContext) {
        if (currentLocation != null 
                && lastTimestamp + 20 * 60 > new Date().getTime()) {
            // 使用上次定位结果，不再重新定位
            pInterface.updateLocation(currentLocation);
        }
        
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        } 
        mLocationClient = new LocationClient(pContext);     //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener(new MyLocationListener());    //注册监听函数
        mLocationClient.start();
        
        return true;
    }
    
    /**
     * 移除监听
     */
    public void removeLocationListener(){
        if (mLocationClient != null 
                && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }
    
    /**
     * 初始化配置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Device_Sensors);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
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
                // 结果暂时缓存
                currentLocation = location;
                lastTimestamp = new Date().getTime();
                
                pInterface.updateLocation(location);
            }
        }
    }
}
