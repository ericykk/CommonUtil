package com.eric.util.cache.redis.message;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * description:消息队列发送对象
 * author:Eric
 * Date:16/9/9
 * Time:15:29
 * version 1.0.0
 */
public class SendMessage {

    private RedisTemplate<String,Object> redisTemplate;

    public SendMessage(){

    }
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送消息
     * @param channel 主题
     * @param message 序列化后信息
     */
    public void sendMessage(String channel, Serializable message){
        redisTemplate.convertAndSend(channel,message);
    }


    /**
     * 添加消息到消息频道
     * @param key 消息频道
     * @param value 消息内容
     * @return
     */
    public Long putToQueue(final String key, final String value) {

        Long l = (Long) this.getRedisTemplate().execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lPush(key.getBytes(Charset.defaultCharset()), value.getBytes(Charset.defaultCharset()));
            }
        });
        return l;
    }


    /**
     * 读取消息频道消息 读取后消息队列消息被取出
     * @param key 消息频道
     * @return
     */
    public String getFromQueue(final String key) {
        byte[] bytes = (byte[]) this.getRedisTemplate().execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lPop(key.getBytes(Charset.defaultCharset()));
            }
        });
        if(bytes != null)
        {
            try{
                return new String(bytes,"Utf-8");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

}
