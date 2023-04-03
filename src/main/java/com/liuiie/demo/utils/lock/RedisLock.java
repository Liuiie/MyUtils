package com.liuiie.demo.utils.lock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * RedisLock
 *
 * @author Liuiie
 * @since 2022/4/28 23:28
 */
public class RedisLock {
    public static final String KEY_PREFIX = "LOCK_KEY_";

    /**
     * 加锁，无阻塞 prefix
     *
     * @param key 键
     * @param expireTime 过期时间
     * @return 加锁结果
     */
    public static boolean lock(String key, long expireTime) {
        String requestId = UUID.randomUUID().toString();
        RedisTemplate redisTemplate = SpringContextUtil.getBean("redisTemplate", RedisTemplate.class);
        // 一次尝试快速失败
        // Set命令返回OK，则证明获取锁成功
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, requestId, expireTime, TimeUnit.SECONDS));
    }

    /**
     * 自旋加锁，无阻塞 prefix
     *
     * @param key 键
     * @param expireTime 过期时间
     * @param timeout 自旋过期时间
     * @return 加锁结果
     */
    public static boolean lock(String key, long expireTime, long timeout) {
        String requestId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        RedisTemplate redisTemplate = SpringContextUtil.getBean("redisTemplate", RedisTemplate.class);
        // 自旋，在一定时间内获取锁，超时则返回错误
        for (;;) {
            // Set命令返回OK，则证明获取锁成功
            Boolean ret = redisTemplate.opsForValue().setIfAbsent(key, requestId, expireTime, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(ret)) {
                return true;
            }
            // 否则循环等待，在timeout时间内仍未获取到锁，则获取失败
            long end = System.currentTimeMillis() - start;
            if (end >= timeout) {
                return false;
            }
        }
    }

    public static void unLock(String... key) {
        RedisTemplate redisTemplate = SpringContextUtil.getBean("redisTemplate", RedisTemplate.class);
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
}
