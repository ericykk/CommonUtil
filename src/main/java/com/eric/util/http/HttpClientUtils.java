package com.eric.util.http;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description:http客户端工具类
 * author:Eric
 * Date:16/9/2
 * Time:16:18
 * version 1.0.0
 */
@Slf4j
public class HttpClientUtils {

    //默认超时时间
    private static final int timeout = 5000;

    /**
     * 默认Httpclient
     *
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        //设置默认超时时间
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(timeout).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig).build();
    }

    /**
     * get请求 返回json字符串
     *
     * @param url 地址
     * @return
     */
    public static String get(String url) {
        return get(url, getHttpClient());
    }

    /**
     * get请求  带参数
     *
     * @param url    地址
     * @param params 参数
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return get(url, params, getHttpClient());
    }

    /**
     * get请求 返回字节数组
     *
     * @param url
     * @return
     */
    public static byte[] getBytes(String url) {
        return getBytes(url, getHttpClient());
    }

    /**
     * get请求 返回字节数组
     *
     * @param url    地址
     * @param params 参数
     * @return
     */
    public static byte[] getBytes(String url, Map<String, String> params) {
        return getBytes(url, params, getHttpClient());
    }


    /**
     * get请求  返回json字符串
     *
     * @param url    地址
     * @param client
     * @return
     */
    public static String get(String url, CloseableHttpClient client) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "text/html; charset=UTF-8");
        String responseResult = null;
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            //请求成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                //读取服务器返回过来的json字符串数据
                responseResult = EntityUtils.toString(entity, "utf-8");
            }
        } catch (Exception e) {
            log.error("发送get请求失败，请求路径:" + url, e);
        } finally {
            try {
                //释放连接
                httpGet.releaseConnection();
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return responseResult;
    }

    /**
     * 发送get请求  获取返回字节数组
     *
     * @param url
     * @param client
     * @return
     */
    public static byte[] getBytes(String url, CloseableHttpClient client) {
        byte[] byteResponse = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            //请求成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                //将服务器返回数据存在字节数组中
                byteResponse = EntityUtils.toByteArray(entity);
            }
        } catch (Exception e) {
            log.error("发送get请求失败,请求地址:" + url, e);
        } finally {
            try {
                //释放连接
                httpGet.releaseConnection();
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return byteResponse;
    }

    /**
     * get请求  返回json字符串
     *
     * @param url    地址
     * @param params 参数
     * @param client
     * @return
     */
    public static String get(String url, Map<String, String> params, CloseableHttpClient client) {
        url = convertGetParams(url, params);
        return HttpClientUtils.get(url, client);
    }

    /**
     * get请求  返回字节数组
     *
     * @param url    地址
     * @param params 参数
     * @param client
     * @return
     */
    public static byte[] getBytes(String url, Map<String, String> params, CloseableHttpClient client) {
        url = convertGetParams(url, params);
        return HttpClientUtils.getBytes(url, client);
    }

    /**
     * post请求 不带参数
     *
     * @param url 地址
     * @return
     */
    public static String post(String url) {
        return post(url, getHttpClient());
    }

    /**
     * post请求  json格式参数
     *
     * @param url  地址
     * @param json json内容
     * @return
     */
    public static String postJson(String url, String json) {
        return postJson(url, json, getHttpClient());
    }


    /**
     * 发送post请求 返回响应json字符串
     *
     * @param url    地址
     * @param client
     * @return
     */
    public static String post(String url, CloseableHttpClient client) {
        // 使用POST方法
        HttpPost method = new HttpPost(url);
        //解决中文乱码问题
        StringEntity requestEntity = new StringEntity(null, "utf-8");
        requestEntity.setContentEncoding("UTF-8");
        requestEntity.setContentType("application/json");
        method.setEntity(requestEntity);
        try {
            CloseableHttpResponse response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "utf-8");
            }
        } catch (Exception e) {
            log.error("发送post请求失败", e);
        } finally {
            try {
                // 释放连接
                method.releaseConnection();
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 发送post请求
     *
     * @param url    地址
     * @param params 参数
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        return post(url, params, getHttpClient());
    }


    /**
     * @param url
     * @param jsonParam
     * @return
     */
    public static String postJos(String url, String jsonParam) {
        return postJson(url, jsonParam, getHttpClient());
    }

    /**
     * 发送post请求 返回响应json字符串
     *
     * @param url    地址
     * @param params 参数
     * @param client
     * @return
     */
    public static String post(String url, Map<String, String> params, CloseableHttpClient client) {
        String responseResult = null;
        HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new UrlEncodedFormEntity(convertPostParams(params)));
            CloseableHttpResponse response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseResult = EntityUtils.toString(entity, "utf-8");
            }
        } catch (Exception e) {
            log.error("发送post请求失败", e);
        } finally {
            try {
                method.releaseConnection();
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return responseResult;
    }

    /**
     * 发送post key value参数格式
     *
     * @param url    地址
     * @param params 参数
     * @param client
     * @return
     */
    public static String postJson(String url, Map<String, String> params, CloseableHttpClient client) {
        String responseResult = null;
        HttpPost method = new HttpPost(url);
        //构建json数据请求
        StringEntity requestEntity = new StringEntity(JSON.toJSONString(params), "utf-8");
        requestEntity.setContentEncoding("UTF-8");
        requestEntity.setContentType("application/json");
        method.setEntity(requestEntity);
        try {
            CloseableHttpResponse response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseResult = EntityUtils.toString(entity, "utf-8");
            }
        } catch (Exception e) {
            log.error("发送postJson请求失败", e);
        } finally {
            try {
                //释放连接
                method.releaseConnection();
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return responseResult;
    }

    /**
     * post请求  json格式请求参数
     *
     * @param url       地址
     * @param jsonParam json参数
     * @param client
     * @return
     */
    public static String postJson(String url, String jsonParam, CloseableHttpClient client) {
        String responseResult = null;
        HttpPost method = new HttpPost(url);
        //构建json数据请求
        StringEntity requestEntity = new StringEntity(jsonParam, "utf-8");
        requestEntity.setContentEncoding("UTF-8");
        requestEntity.setContentType("application/json");
        method.setEntity(requestEntity);
        try {
            CloseableHttpResponse response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseResult = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            log.error("发送postJson请求失败，请求参数：" + jsonParam, e);
        } finally {
            try {
                //释放连接
                method.releaseConnection();
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return responseResult;
    }

    /**
     * 封装get请求参数
     *
     * @param url
     * @param params
     * @return
     */
    private static String convertGetParams(String url, Map<String, String> params) {
        StringBuilder queryString = new StringBuilder(url);
        if (url.contains("?")) {
            queryString.append("&");
        } else {
            queryString.append("?");
        }
        int index = 0;
        int size = params.size();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryString.append(entry.getKey() + "=" + entry.getValue());
            if (index != size - 1) {
                queryString.append("&");
            }
            index++;
        }
        return queryString.toString();
    }

    /**
     * post请求参数转化
     *
     * @param params 参数
     * @return
     */
    private static List<BasicNameValuePair> convertPostParams(Map<String, String> params) {
        List<BasicNameValuePair> paramPairList = new ArrayList<>(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramPairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return paramPairList;
    }

}
