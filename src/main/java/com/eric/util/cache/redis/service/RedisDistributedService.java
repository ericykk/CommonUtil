package com.eric.util.cache.redis.service;

import com.eric.util.cache.redis.repository.RedisDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * description:分布式redis客户端
 * author:Eric
 * Date:16/9/23
 * Time:18:20
 * version 1.0.0
 */
@Slf4j
@Service
public class RedisDistributedService {

    @Resource
    private RedisDataSourceService redisDataSourceService;


    /**
     * 设置值
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        String result = null;
        ShardedJedis shardedJedis = redisDataSourceService.getRedisClient();
        if (shardedJedis != null) {
            try {
                result = shardedJedis.set(key, value);
            } catch (Exception e) {
                log.error("set operation fail", e);
            } finally {
                shardedJedis.close();
            }
        }
        return result;
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String result = null;
        ShardedJedis shardedJedis = redisDataSourceService.getRedisClient();
        if (shardedJedis != null) {
            try {
                result = shardedJedis.get(key);
            } catch (Exception e) {
                log.error("get value fail", e);
            } finally {
                shardedJedis.close();
            }
        }
        return result;
    }

}
