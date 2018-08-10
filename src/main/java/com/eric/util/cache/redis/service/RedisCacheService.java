package com.eric.util.cache.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * description:单个redis服务
 * author:Eric
 * Date:16/9/21
 * Time:15:54
 * version 1.0.0
 */
@Slf4j
@Service
public class RedisCacheService {


    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 批量删除key
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 删除对应的key的值
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void remove(final String key) {
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否存在对应的value
     *
     * @param key
     * @return
     */
    public boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object value;
        ValueOperations operations = redisTemplate.opsForValue();
        value = operations.get(key);
        return value;
    }


    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean set(final String key, Object value) {
        boolean isSuccess = false;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("设置缓存失败", e);
        }

        return isSuccess;

    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @param expireTime 缓存时间
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean set(final String key, Object value, Long expireTime) {

        boolean isSuccess = false;

        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            //设置缓存
            isSuccess = redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

}
