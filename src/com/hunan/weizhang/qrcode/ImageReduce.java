package com.hunan.weizhang.qrcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageReduce {

    /**
     * 获取图片RGB数组
     * 
     * @param filePath
     * @return
     */
    public static int[][] getImageGRB(String imgStr) {
        int[][] result = null;
        try {
            Bitmap bufImg = base64ToBitmap(imgStr);
            int height = bufImg.getHeight();
            int width = bufImg.getWidth();
            result = new int[width][height];
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int pixel = bufImg.getPixel(i, j);
                    int[] rgb = new int[3];
                    rgb[0] = (pixel & 0xff0000) >> 16;
                    rgb[1] = (pixel & 0xff00) >> 8;
                    rgb[2] = (pixel & 0xff);
                    if (rgb[0] < 125 || rgb[1] < 125 || rgb[2] < 125) {
                        result[i][j] = 1;
                    } else {
                        result[i][j] = 0;
                    }
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return result;
    }

    /*
     * 二值化图片降噪
     * 
     * @param erzhiArray二值化数组
     */
    public static int[][] reduceZao(int[][] erzhiArray) {
        int[][] data = erzhiArray;
        int gao = erzhiArray.length;
        int chang = erzhiArray[0].length;

        int[][] jiangzaoErzhiArray = new int[gao][chang];

        for (int i = 0; i < gao; i++) {
            for (int j = 0; j < chang; j++) {
                int num = 0;
                if (data[i][j] == 1) {
                    // 上
                    if (i - 1 >= 0) {
                        num = num + data[i - 1][j];
                    }
                    // 下
                    if (i + 1 < gao) {
                        num = num + data[i + 1][j];
                    }
                    // 左
                    if (j - 1 >= 0) {
                        num = num + data[i][j - 1];
                    }
                    // 右
                    if (j + 1 < chang) {
                        num = num + data[i][j + 1];
                    }
                    // 上左
                    if (i - 1 >= 0 && j - 1 >= 0) {
                        num = num + data[i - 1][j - 1];
                    }
                    // 上右
                    if (i - 1 >= 0 && j + 1 < chang) {
                        num = num + data[i - 1][j + 1];
                    }
                    // 下左
                    if (i + 1 < gao && j - 1 >= 0) {
                        num = num + data[i + 1][j - 1];
                    }
                    // 下右
                    if (i + 1 < gao && j + 1 < chang) {
                        num = num + data[i + 1][j + 1];
                    }
                }

                if (num < 1) {
                    jiangzaoErzhiArray[i][j] = 0;
                } else {
                    jiangzaoErzhiArray[i][j] = 1;
                }
            }
        }
        return jiangzaoErzhiArray;
    }

    /**
     * 分隔字符
     * 
     * @param imageArray
     */
    public static List<String> cuttingImage(String fileName, int[][] imageArray) {
        // 优先纵向切割字符
        List<int[][]> charArrayList = cuttingVerticalImage(imageArray);
        if (charArrayList.size() != 4) {
            System.out.println("数组分隔后不是4个字符");
        }
        
        List<String> resultList = new ArrayList<String>();
        for (int[][] item : charArrayList) {
            resultList.add(deleteBlanks(item));
        }
        
        return resultList;
    }
    
    /**
     * 切割并匹配字符
     * 
     * @param resultList
     * @param imageArray
     * @return
     */
    public static String cuttingImage(Map<String, String> resultList, int[][] imageArray) {
        List<Integer> startArray = new ArrayList<Integer>();
        List<Integer> endArray = new ArrayList<Integer>();

        int height = imageArray[0].length;
        
        int lastTotal = 0;
        for (int i = 0; i < imageArray.length; i++) {
            int[] iteam = imageArray[i];
            int total = 0;
            for (int j = 0; j < iteam.length; j++) {
                total = total + iteam[j];
            }
            if (total != 0) {
                // 有效列
                if (lastTotal != 0) {
                    // 上一列也是有效列，忽略该列
                    continue;
                } else {
                    // 上一列是无效列，该列为下一个字符的开始
                    startArray.add(i);
                }
            } else {
                // 无效列
                if (lastTotal == 0) {
                    // 上一列也是无效列，忽略该列
                    continue;
                } else {
                    // 上一列是有效的，那么该列是上一个字符的结束
                    endArray.add(i);
                }
            }
            lastTotal = total;
        }
        
        if (startArray.size() == 4 && endArray.size() == 3) {
            endArray.add(imageArray.length);
        }
        
        if (startArray.size() != endArray.size()) {
            // 字符分隔出错
            return null;
        }
        
        String result = "";
        
        for (int k = 0; k < startArray.size(); k++) {
            // 遍历字符
            int width = endArray.get(k) - startArray.get(k);
            
            if (width >= 15 && startArray.size() < 4) {
                // 字符超出单个字符的阀值，且切割后的字符小于4个
                // m=14；W=15；w=13；Q=12；M=13
                for (int i = 10; i < 16; i++) {
                    int[][] temp = exchangeCharArray(startArray.get(k), startArray.get(k) + i, height, imageArray);
                    temp = reduceZao(temp);
                    String text = deleteBlanks(temp);
                    String similarChar = findSimilarChar(text, resultList);
                    if (similarChar != null) { // 切割字母第一个匹配到
                        result = result + similarChar;
                        text = deleteBlanks(reduceZao(exchangeCharArray(startArray.get(k) + i, endArray.get(k), height, imageArray)));
                        similarChar = findSimilarChar(text, resultList);
                        if ( similarChar != null) {
                            result = result + similarChar;
                        } else {
                            // 再次尝试
                            text = deleteBlanks(reduceZao(exchangeCharArray(startArray.get(k) + i + 1, endArray.get(k), height, imageArray)));
                            similarChar = findSimilarChar(text, resultList);
                            if ( similarChar != null) {
                                result = result + similarChar;
                            } 
                        }
                        break;
                    }
                }
            } else {
                String text = deleteBlanks(exchangeCharArray(startArray.get(k), endArray.get(k), height, imageArray));
                String similarChar = findSimilarChar(text, resultList);
                if (similarChar != null) {
                    result = result + similarChar;
                }
            }
        }
        return result;
    }
    
    /**
     * 匹配样本字符
     * 
     * @param text
     * @param resultList
     * @return
     */
    private static String findSimilarChar(String text, Map<String, String> resultList) {
        // 完全匹配
        if (resultList.containsKey(text)) {
            return resultList.get(text);
        }
        
        String value = null;
        int minDistance = 0;
        // 尝试相似度匹配
        for(String key : resultList.keySet()) {
            int distance = SimilarUtil.compute_distance(text, key);
            if (minDistance == 0) {
                minDistance = distance;
                value = resultList.get(key);
            } else if (distance < minDistance) {
                minDistance = distance;
                value = resultList.get(key);
            }
        }
        
        if (minDistance < 20) {
            return value;
        }
        return null;
    }
    
    
    /**
     * 竖线切割
     * 
     * @param imageArray
     */
    public static List<int[][]> cuttingVerticalImage(int[][] imageArray) {
        List<Integer> startArray = new ArrayList<Integer>();
        List<Integer> endArray = new ArrayList<Integer>();

        int height = imageArray[0].length;
        
        int lastTotal = 0;
        for (int i = 0; i < imageArray.length; i++) {
            int[] iteam = imageArray[i];
            int total = 0;
            for (int j = 0; j < iteam.length; j++) {
                total = total + iteam[j];
            }
            if (total != 0) {
                // 有效列
                if (lastTotal != 0) {
                    // 上一列也是有效列，忽略该列
                    continue;
                } else {
                    // 上一列是无效列，该列为下一个字符的开始
                    startArray.add(i);
                }
            } else {
                // 无效列
                if (lastTotal == 0) {
                    // 上一列也是无效列，忽略该列
                    continue;
                } else {
                    // 上一列是有效的，那么该列是上一个字符的结束
                    endArray.add(i);
                }
            }
            lastTotal = total;
        }
        
        if (startArray.size() == 4 && endArray.size() == 3) {
            endArray.add(imageArray.length);
        }
        
        List<int[][]> charArrayList = new ArrayList<int[][]>();

        if (startArray.size() != endArray.size()) {
            // 字符分隔出错
            return charArrayList;
        }
        
        for (int k = 0; k < startArray.size(); k++) {
            // 遍历字符
            charArrayList.add(exchangeCharArray(startArray.get(k), endArray.get(k), height, imageArray));
        }
        return charArrayList;
    }
    
    /**
     * 纵向切割后分离小数组
     * 
     * @param start
     * @param end
     * @param height
     * @param imageArray
     * @return
     */
    private static int[][] exchangeCharArray(int start, int end, int height, int[][] imageArray) {
        int width = end - start;
        int[][] charArray = new int[width][height];
        int hangIndex = 0;
        for (int i = start; i < end; i++) {
            // 遍历行
            for (int j = 0; j < height; j++) {
                // 遍历列
                charArray[hangIndex][j] = imageArray[i][j];
            }
            hangIndex++;
        }
        return charArray;
    }
    
    /**
     * 删除横向空白区
     * 
     * @param imageArray
     * @return
     */
    public static String deleteBlanks(int[][] imageArray) {
        int width = imageArray.length;
        int height = imageArray[0].length;
        
        int start = 0;
        int end = height;
        int lastTotal = 0;
        
        for (int j = 0; j < height; j++) {
            int total = 0;
            for (int i = 0; i < width; i++) {
                total = total + imageArray[i][j];
            }
            if (total != 0) {
                // 有效行
                if (lastTotal == 0 ) {
                    // 此行为数组的开始行
                    if (start != 0 && start < j) {
                        // 字母不连续的开始，忽略
                        end = height;
                    } else {
                        start = j;
                    }
                }
            } else {
                // 无效行
                if (lastTotal != 0) {
                    end = j;
                }
            }
            lastTotal = total;
        }
        
        StringBuilder result = new StringBuilder();
        for (int j = start; j < end; j++) {
            for (int i = 0; i < width; i++) {
                result.append(imageArray[i][j]);
            }
        }
        
        return result.toString();
    }
    
    /*
     *归一化处理,针对一个个的数字,即去除字符周围的白点
     *@param singleArray 二值化数组
     */
    public static int[][] getJinsuo(int[][] singleArray){
        int gao = singleArray.length;
        int kuan = singleArray['0'].length;
        
        int dianCount = 0;
        int shangKuang = 0;
        int xiaKuang = 0;
        int zuoKuang = 0;
        int youKuang = 0;
        //从上到下扫描
        for(int i=0; i < gao; ++i){
            for(int j=0; j < kuan; ++j){
                if( singleArray[i][j] == 1){
                    dianCount++;
                }
            }
            if(dianCount>1){
                shangKuang = i;
                dianCount = 0;
                break;
            }
        }
        //从下到上扫描
        for(int i=gao-1; i > -1; i--){
            for(int j=0; j < kuan; ++j){
                if( singleArray[i][j] == 1){
                    dianCount++;
                }
            }
            if(dianCount>1){
                xiaKuang = i;
                dianCount = 0;
                break;
            }
        }
        //从左到右扫描
        for(int i=0; i < kuan; ++i){
            for(int j=0; j < gao; ++j){
                if( singleArray[j][i] == 1){
                    dianCount++;
                }
            }
            if(dianCount>1){
                zuoKuang = i;
                dianCount = 0;
                break;
            }
        }
        //从右到左扫描
        for(int i=kuan-1; i > -1; --i){
            for(int j=0; j < gao; ++j){
                if( singleArray[j][i] == 1){
                    dianCount++;
                }
            }
            if(dianCount>1){
                youKuang = i;
                dianCount = 0;
                break;
            }
        }
        int[][] rearr = new int[xiaKuang-shangKuang+1][youKuang-zuoKuang+1];
        for(int i=0;i<xiaKuang-shangKuang+1;i++){
            for(int j=0;j<youKuang-zuoKuang+1;j++){
                rearr[i][j] = singleArray[shangKuang+i][zuoKuang+j];
            }
        }
        return rearr;
    }
    
    /**
     * base64转为bitmap
     * 
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.NO_WRAP);  
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length); 
    }
}
