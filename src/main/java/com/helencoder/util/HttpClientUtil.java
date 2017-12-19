package com.helencoder.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient使用示例
 *
 * Created by zhenghailun on 2017/11/18.
 */
public class HttpClientUtil {

    /**
     * Get请求(不带参数)
     *
     * @param url 请求的url地址
     * @return String 请求响应
     */
    public static String get(String url) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String response = "";
        try {
            // 创建HttpGet
            HttpGet httpget = new HttpGet(url);
            // 执行get请求.
            CloseableHttpResponse httpResponse = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    response = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * Get请求(带参数)
     *
     * @param url 请求的url地址
     * @param paramsMap 参数名作为键,参数值作为键值
     * @return String 请求响应
     */
    public static String get(String url, Map<String, String> paramsMap) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String response = "";
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                sb.append(entry.getKey() + "=" + entry.getValue() + "&");
            }

            String requestUrl = url + "?" + sb.toString().substring(0, sb.toString().lastIndexOf("&"));
            // 创建HttpGet
            HttpGet httpget = new HttpGet(requestUrl);
            // 执行get请求.
            CloseableHttpResponse httpResponse = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    response = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * Post请求
     *
     * @param url 请求的url地址
     * @param paramsMap 参数名作为键,参数值作为键值
     * @return String 请求响应
     */
    public static String post(String url, Map<String, String> paramsMap) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httpPost
        HttpPost httppost = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        String response = "";
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(paramsList, Consts.UTF_8);
            httppost.setEntity(uefEntity);
            CloseableHttpResponse httpResponse = httpclient.execute(httppost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    response = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * Post请求(JSON数据)
     *
     * @param url
     * @param json
     * @return
     */
    public static String postWithJSON(String url, JSONObject json) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httpPost
        HttpPost httppost = new HttpPost(url);
        String response = "";
        try {
            StringEntity sEntity = new StringEntity(json.toString());
            sEntity.setContentEncoding("UTF-8");
            sEntity.setContentType("application/json"); //发送json数据需要设置contentType
            httppost.setEntity(sEntity);
            CloseableHttpResponse httpResponse = httpclient.execute(httppost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    response = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * 下载url图片
     *
     * @param imgUrl 图片url
     * @param storeFilePath 图片存储路径
     */
    public static void downloadImage(String imgUrl, String storeFilePath) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String response = "";
        try {
            // 创建httpGet
            HttpGet httpget = new HttpGet(imgUrl);
            // 执行get请求.
            CloseableHttpResponse httpResponse = httpclient.execute(httpget);
            // 文件存储
            FileOutputStream output = new FileOutputStream(new File(storeFilePath));
            try {
                // 获取响应实体
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    try {
                        byte b[] = new byte[1024];
                        int j = 0;
                        while( (j = instream.read(b))!=-1){
                            output.write(b,0,j);
                        }
                        output.flush();
                        output.close();
                    } catch (IOException ex) {
                        // In case of an IOException the connection will be released
                        // back to the connection manager automatically
                        throw ex;
                    } catch (RuntimeException ex) {
                        // In case of an unexpected exception you may want to abort
                        // the HTTP request in order to shut down the underlying
                        // connection immediately.
                        httpget.abort();
                        throw ex;
                    } finally {
                        // Closing the input stream will trigger connection release
                        try { instream.close(); } catch (Exception ignore) {}
                    }
                }
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}




