package com.hunan.weizhang.utils;

import com.baidu.location.BDLocation;

public interface LocationHelper {
    /**
     * 位置信息发生改变
     * 
     * @param location
     */
    void updateLocation(BDLocation location);
}
