package com.hunan.weizhang.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManagerFactory {
    
    private static int corePoolSize = 2;

    private static int maxPoolSize = 2;

    private static int keepAliveTime = 300;
    
    private static ThreadPoolExecutor threadPoolManager;
    
    static {
        threadPoolManager = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, 
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), 
                new ThreadPoolExecutor.DiscardPolicy());
    }
        
    public static ThreadPoolExecutor getInstance() {
        return threadPoolManager;
    }
}
