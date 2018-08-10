package com.eric.util.cache.redis.interceptor;

import com.eric.daily.cache.redis.service.RedisCacheService;
import com.eric.daily.configuration.annotation.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MethodCacheInterceptor implements MethodInterceptor {

    @Resource
    private RedisCacheService cacheService;

    /**
     * redis和数据库同步
     * 监听数据库insert delete update操作 然后更新redis缓存
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object value;

        Method method = invocation.getMethod();
        //获取缓存注解
        RedisCache redisCache = method.getDeclaredAnnotation(RedisCache.class);
        //判断是否需要缓存
        if (redisCache == null) {
            return invocation.proceed();
        }
        //方法名
        String methodName = method.getName();
        //类型名
        String targetName = invocation.getThis().getClass().getName();
        Object[] arguments = invocation.getArguments();
        //获取缓存key
        String key = getCacheKey(targetName, methodName, arguments);

        if (cacheService.hasKey(key)) {
            return cacheService.get(key);
        }
        ExecutorService executorService = null;

        try {
            value = invocation.proceed();
            if (value != null) {
                executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
                final String cacheKey = key;
                final Object cacheValue = value;
                executorService.execute(() -> cacheService.set(cacheKey, cacheValue, redisCache.expireTime()));
            }
        } catch (Exception e) {
            log.error("redis缓存失败：[key=" + key + "]", e);
            return invocation.proceed();
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
        return value;
    }


    /**
     * 生成缓存key
     *
     * @param targetName
     * @param methodName
     * @param arguments
     * @return
     */
    private String getCacheKey(String targetName, String methodName, Object[] arguments) {
        StringBuilder keySb = new StringBuilder();
        keySb.append(targetName).append("_").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (Object argument : arguments) {
                keySb.append("_").append(argument);
            }
        }
        return keySb.toString();
    }
}
