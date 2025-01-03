package com.liuiie.demo.utils.lock;

import com.liuiie.demo.utils.common.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtil
 *
 * @author Liuiie
 * @since 2025/1/3 15:19
 */
@Slf4j
public class RedisUtil {
    public static final String BEAN_REDIS_NAME = "redisTemplate";

    /**
     * 使用 Holder 模式进行懒加载
     */
    private static class RedisTemplateHolder {
        private static final RedisTemplate<String, Object> INSTANCE;

        static {
            try {
                INSTANCE = SpringContextUtil.getBean(BEAN_REDIS_NAME, RedisTemplate.class);
                if (INSTANCE == null) {
                    throw new IllegalStateException("RedisTemplate 未正确初始化");
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    private static RedisTemplate<String, Object> getRedisTemplate() {
        return RedisUtil.RedisTemplateHolder.INSTANCE;
    }

    /**
     * 写入缓存
     *
     * @param key   关键字
     * @param value 值
     * @return 如果写入成功，则返回 true；否则返回 false
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = getRedisTemplate().opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("写入缓存异常: {}", e.getMessage());
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key   关键字
     * @param value 值
     * @param expireTime 过期时间
     * @return 如果写入成功，则返回 true；否则返回 false
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = getRedisTemplate().opsForValue();
            operations.set(key, value);
            getRedisTemplate().expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error("写入缓存设置失效异常: {}", e.getMessage());
        }
        return result;
    }

    /**
     * 更新缓存
     *
     * @param key   关键字
     * @param value 值
     * @return 如果更新成功，则返回 true；否则返回 false
     */
    public boolean getAndSet(final String key, String value) {
        boolean result = false;
        try {
            getRedisTemplate().opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            log.error("更新缓存异常: {}", e.getMessage());
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys 关键字集合
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern 模式
     */
    public void removePattern(final String pattern) {
        Set<String> keys = getRedisTemplate().keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            getRedisTemplate().delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key 关键字
     */
    public void remove(final String key) {
        if (exists(key) != null && exists(key)) {
            getRedisTemplate().delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key 关键字
     * @return 存在，则返回 true；否则返回 false
     */
    public Boolean exists(final String key) {
        return getRedisTemplate().hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key 关键字
     * @return 值
     */
    public Object get(final String key) {
        ValueOperations<String, Object> operations = getRedisTemplate().opsForValue();
        return operations.get(key);
    }

    /**
     * 哈希 添加
     *
     * @param key 关键字
     * @param hashKey 哈希关键字
     * @param value 值
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = getRedisTemplate().opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希获取数据
     *
     * @param key 关键字
     * @param hashKey 哈希关键字
     * @return 值
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = getRedisTemplate().opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 列表添加
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = getRedisTemplate().opsForList();
        list.rightPush(k, v);
    }

    /**
     * 列表获取
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = getRedisTemplate().opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 集合添加
     */
    public void addSet(String key, Object value) {
        SetOperations<String, Object> set = getRedisTemplate().opsForSet();
        set.add(key, value);
    }

    /**
     * 删除集合下的所有值
     */
    public void removeSetAll(String key) {
        SetOperations<String, Object> set = getRedisTemplate().opsForSet();
        Set<Object> objectSet = set.members(key);
        if (objectSet != null && !objectSet.isEmpty()) {
            for (Object o : objectSet) {
                set.remove(key, o);
            }
        }
    }

    /**
     * 判断set集合里面是否包含某个元素
     */
    public Boolean isMember(String key, Object member) {
        SetOperations<String, Object> set = getRedisTemplate().opsForSet();
        return set.isMember(key, member);
    }

    /**
     * 集合获取
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = getRedisTemplate().opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     */
    public void zAdd(String key, Object value, double source) {
        ZSetOperations<String, Object> zset = getRedisTemplate().opsForZSet();
        zset.add(key, value, source);
    }

    /**
     * 有序集合获取指定范围的数据
     */
    public Set<Object> rangeByScore(String key, double source, double source1) {
        ZSetOperations<String, Object> zSet = getRedisTemplate().opsForZSet();
        return zSet.rangeByScore(key, source, source1);
    }

    /**
     * 有序集合升序获取
     */
    public Set<Object> range(String key, Long source, Long source1) {
        ZSetOperations<String, Object> zSet = getRedisTemplate().opsForZSet();
        return zSet.range(key, source, source1);
    }

    /**
     * 有序集合降序获取
     */
    public Set<Object> reverseRange(String key, Long source, Long source1) {
        ZSetOperations<String, Object> zSet = getRedisTemplate().opsForZSet();
        return zSet.reverseRange(key, source, source1);
    }
}
