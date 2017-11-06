package com.helencoder.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http请求类
 *
 * Created by zhenghailun on 2017/11/6.
 */
public class HttpUtil {

    /**
     * GET请求
     *
     * @param url 请求地址
     * @param param 请求参数
     */
    public static void get(String url, String param) {
        String path = url + "?" + param;
        System.out.println(path);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(path);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            System.out.println(httpResponse);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                String detail = EntityUtils.toString(entity, "utf-8");
                System.out.println(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * post请求
     *
     * @param url 请求地址
     * @param paramsMap 请求参数（key -> value）
     */
    public static void post(String url, Map<String, String> paramsMap) {
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity2 = httpResponse.getEntity();
                String details = EntityUtils.toString(entity2, "UTF-8");
                System.out.println(details);
            }
        }catch(Exception e){e.printStackTrace();}
    }

}
