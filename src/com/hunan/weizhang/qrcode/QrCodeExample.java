package com.hunan.weizhang.qrcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class QrCodeExample {
    private static Map<String, String> codeExampleMap = null;
    
    /**
     * 解析验证码
     * 
     * @param imageData
     * @return
     */
    public static String qrCode(String imageData) {
        if (codeExampleMap == null) {
            return null;
        }
        // 图片二值化
        int[][] erzhiArray = ImageReduce.getImageGRB(imageData);
        // 图片去除噪点
        int[][] jiangzaoErzhiArray = ImageReduce.reduceZao(erzhiArray);
        return ImageReduce.cuttingImage(codeExampleMap, jiangzaoErzhiArray);
    }
    
    /**
     * 初始化
     * 
     * @param is
     */
    public static Map<String, String> init(InputStream is) {
        if (codeExampleMap != null) {
            return codeExampleMap;
        }
        
        codeExampleMap = new HashMap<String, String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while ((str = br.readLine()) != null) {
                String[] temp = str.split("=");
                if (temp.length == 2) {
                    codeExampleMap.put(temp[0], temp[1]);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return codeExampleMap;
    }
}
