package com.eric.util.cache.redis.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

/**
 * description:
 * author:Eric
 * Date:16/9/27
 * Time:15:25
 * version 1.0.0
 */
@Slf4j
@Repository
public class RedisDataSourceService implements RedisDataSource {

    @Resource
    private ShardedJedisPool shardedJedisPool;

    @Override
    public ShardedJedis getRedisClient() {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedJedisPool.getResource();
        } catch (Exception e) {
            log.error("get redis resource fail,the reason is" + e.getMessage());
        }
        return shardedJedis;
    }

    @Override
    public void returnResource(ShardedJedis shardedJedis) {
        shardedJedis.close();
    }
}
