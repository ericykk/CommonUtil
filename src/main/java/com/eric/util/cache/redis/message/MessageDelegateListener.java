package com.eric.util.cache.redis.message;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * description:订阅消息处理
 * author:Eric
 * Date:16/9/9
 * Time:15:40
 * version 1.0.0
 */
public class MessageDelegateListener {

    /**
     * 订阅消息处理
     * @param message
     */
    public void handleMessage(Serializable message) {
        if(message == null){
            System.out.println("null");
        } else if(message.getClass().isArray()){
            System.out.println(Arrays.toString((Object[])message));
        } else if(message instanceof List<?>) {
            System.out.println(message);
        } else if(message instanceof Map<? , ?>) {
            System.out.println(message);
        } else {
            System.out.println(ToStringBuilder.reflectionToString(message));
        }
    }
}
