package com.eric.util.cache.redis.repository;


import redis.clients.jedis.ShardedJedis;

/**
 * description:
 * author:Eric
 * Date:16/9/27
 * Time:15:19
 * version 1.0.0
 */
public interface RedisDataSource {

    /**
     * 获取redis客户端服务
     * @return
     */
    ShardedJedis getRedisClient();

    /**
     * 资源回收
     * @param shardedJedis
     */
    void returnResource(ShardedJedis shardedJedis);

}
