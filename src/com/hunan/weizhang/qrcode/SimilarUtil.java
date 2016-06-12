package com.hunan.weizhang.qrcode;

public class SimilarUtil {
    
    public static int min(int a, int b, int c) {  
        if(a < b) {  
            if(a < c)  
                return a;  
            else   
                return c;  
        } else {  
            if(b < c)  
                return b;  
            else   
                return c;  
        }  
    }
    
    /**
     * 计算字符串的距离
     * 
     * @param textA
     * @param textB
     * @return
     */
    public static int compute_distance(String textA, String textB) {
        char[] strA = textA.toCharArray();
        char[] strB = textB.toCharArray();
        int len_a = strA.length;  
        int len_b = strB.length;  
        int[][] temp = new int[len_a + 1][len_b + 1];
        
        
        int i, j;  
      
        for(i = 1; i <= len_a; i++) {  
            temp[i][0] = i;  
        }  
      
        for(j = 1; j <= len_b; j++) {  
            temp[0][j] = j;  
        }  
      
        temp[0][0] = 0;  
      
        for(i = 1; i <= len_a; i++) {  
            for(j = 1; j <= len_b; j++) {  
                if(strA[i -1] == strB[j - 1]) {  
                    temp[i][j] = temp[i - 1][j - 1];  
                } else {  
                    temp[i][j] = min(temp[i - 1][j], temp[i][j - 1], temp[i - 1][j - 1]) + 1;  
                }  
            }  
        }  
        return temp[len_a][len_b];  
    }
//    
//    public static void main(String[] args) {
//        String a = "110000111000011110000111000011011000111000110011000101000110011001101100110011001101100110001101101101100001101101101100001101101101100001101001101100000111000111000000111000111000000111000111000";  
//        String b = "11000011100001110000111000010110001110001101100010100011011001101100110110011011001100110110110110001101101101100011011011011000110100110110000111000111000001110001110000011100011100";  
//
//        int distance = compute_distance(a,  b);  
//        System.out.println(distance);
//    }  
}
