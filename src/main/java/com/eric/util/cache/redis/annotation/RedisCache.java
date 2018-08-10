package com.eric.util.cache.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description:redis缓存注解
 * author:Eric
 * Date:16/9/21
 * Time:16:38
 * version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /**
     * 缓存时间
     *
     * @return
     */
    long expireTime() default 7200L;
}
