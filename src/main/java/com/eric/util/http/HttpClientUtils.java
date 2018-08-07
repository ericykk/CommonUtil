package com.eric.util.http;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description:http客户端工具类
 * author:Eric
 * Date:16/9/2
 * Time:16:18
 * version 1.0.0
 */
public class HttpClientUtils {


    public static void main(String[] args) throws Exception {
        postMethod();
    }

    public static void getMethod() throws  Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/ericykk/CommonUtil");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        try {
            System.out.println(httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            EntityUtils.consume(entity);
        }finally {
            httpResponse.close();
        }
    }


    public static void postMethod() throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://github.com/login");
        //封装参数
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("username","test"));
        nameValuePairList.add(new BasicNameValuePair("password","test"));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try{
            System.out.println(httpResponse.getStatusLine());
            HttpEntity entity = httpResponse.getEntity();
            EntityUtils.consume(entity);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            httpResponse.close();
        }


    }

}
