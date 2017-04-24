package com.hunan.weizhang.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
    
    /**
     * post请求
     * 
     * @param url 请求url
     * @param paramsMap  请求参数
     * @return
     */
    public static String postResponse(String url, Map<String, Object> paramsMap, Map<String, String> headerMap) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (paramsMap != null) {
                for (String key : paramsMap.keySet()) {
                    params.add(new BasicNameValuePair(key,
                            paramsMap.get(key).toString()));
                }
            }
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000); 
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    httpost.setHeader(key, headerMap.get(key));
                }
            }
            
            httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();
            String htmlStr = null;
            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
                htmlStr = EntityUtils.toString(entity);
                entity.consumeContent();
            }
            return htmlStr;

        } catch (Exception e) {
            e.printStackTrace();
//            Log.i(url, e.getMessage());
        }
        return null;
    }

    
    /**
     * get请求
     * 
     * @param url 请求url
     * @return
     */
    public static String getResponse(String url, Map<String, String> headerMap) {
        try {
            HttpResponse response = getHttpResponse(url, headerMap);
            if (response == null 
                    || response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            String htmlStr = null;
            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
                htmlStr = EntityUtils.toString(entity, "UTF-8");
                entity.consumeContent();
            }
            
            return htmlStr;

        } catch (Exception e) {
            // TODO 
            e.printStackTrace();
        }
        return null;
    }
    
    public static HttpResponse getHttpResponse(String url, Map<String, String> headerMap) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            URI uri = new URI(url);
            HttpGet httpGet = new HttpGet(uri);
            httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000); 
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    httpGet.setHeader(key, headerMap.get(key));
                }
            }
            
            HttpResponse response = httpclient.execute(httpGet);
            if (response == null 
                    || response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            return response;
            
        } catch (Exception e) {
            // TODO 
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getResponseCookie(String url, Map<String, String> headerMap) {
        try {
            HttpResponse response = getHttpResponse(url, headerMap);
            if (response == null 
                    || response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            
            Header[] headers = response.getHeaders("Set-Cookie");
            String result = "";
            for (Header header : headers) {
                String value = header.getValue();
                result = result + value.split(";")[0] +";";
            }
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            // TODO 
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * get请求
     * 
     * @param url 请求url
     * @return
     */
    public static String getHttpsResponse(String url, Map<String, String> headerMap) {
        try {
            URL getUrl = new URL(url);
            URLConnection urlConnection = getUrl.openConnection();
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    urlConnection.addRequestProperty(key, headerMap.get(key));
                }
            }
            
            InputStream in = urlConnection.getInputStream();
            
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
