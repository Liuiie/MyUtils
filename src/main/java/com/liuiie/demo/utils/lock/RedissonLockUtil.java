package com.liuiie.demo.utils.lock;

import com.liuiie.demo.utils.common.SpringContextUtil;
import com.liuiie.demo.utils.thread.ThreadUtil;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 使用 Redisson 实现的分布式锁工具类。
 *      包含了普通锁、读锁和写锁的支持。
 *
 * @author Liuiie
 * @since 2025/1/2 17:36
 */
@Log4j2
public class RedissonLockUtil {
    private static final String REDIS_LOCK_PREFIX = "LOCK_KEY_";

    /**
     * 使用 Holder 模式进行懒加载
     */
    private static class RedissonClientHolder {
        private static final RedissonClient INSTANCE;

        static {
            try {
                INSTANCE = SpringContextUtil.getBean(RedissonClient.class);
                if (INSTANCE == null) {
                    throw new IllegalStateException("RedissonClient 未正确初始化");
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    private static RedissonClient getRedissonClient() {
        return RedissonClientHolder.INSTANCE;
    }

    /**
     * 尝试获取普通锁
     *
     * @param lockName 锁名称
     * @param leaseTime 锁的最大持有时间（单位：秒）
     * @param waitTime  加锁超时时长（单位：秒）
     * @return 是否成功获取锁
     */
    public static boolean tryLock(String lockName, int leaseTime, int waitTime) {
        RLock lock = getRedissonClient().getLock(REDIS_LOCK_PREFIX + lockName);
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (isLocked) {
                log.info("线程: {} 成功获取普通锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
            } else {
                log.warn("线程: {} 未能获取普通锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
            }
            return isLocked;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程: {} 尝试获取普通锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), lockName, e);
            return false;
        }
    }

    /**
     * 释放普通锁
     *
     * @param lockName 锁名称
     */
    public static void unlock(String lockName) {
        RLock lock = getRedissonClient().getLock(REDIS_LOCK_PREFIX + lockName);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.info("线程: {} 成功释放普通锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
        } else {
            log.warn("线程: {} 没有持有普通锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
        }
    }

    /**
     * 尝试获取读锁
     *
     * @param lockName 锁名称
     * @param leaseTime 锁的最大持有时间（单位：秒）
     * @param waitTime  加锁超时时长（单位：秒）
     * @return 是否成功获取读锁
     */
    public static boolean tryReadLock(String lockName, int leaseTime, int waitTime) {
        RReadWriteLock rwLock = getRedissonClient().getReadWriteLock(REDIS_LOCK_PREFIX + lockName);
        RLock readLock = rwLock.readLock();
        try {
            boolean isLocked = readLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (isLocked) {
                log.info("线程: {} 成功获取读锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
            } else {
                log.warn("线程: {} 未能获取读锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
            }
            return isLocked;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程: {} 尝试获取读锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), lockName, e);
            return false;
        }
    }

    /**
     * 释放读锁
     *
     * @param lockName 锁名称
     */
    public static void unlockRead(String lockName) {
        RReadWriteLock rwLock = getRedissonClient().getReadWriteLock(REDIS_LOCK_PREFIX + lockName);
        RLock readLock = rwLock.readLock();
        if (readLock.isHeldByCurrentThread()) {
            readLock.unlock();
            log.info("线程: {} 成功释放读锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
        } else {
            log.warn("线程:{} 没有持有读锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
        }
    }

    /**
     * 尝试获取写锁
     *
     * @param lockName 锁名称
     * @param leaseTime 锁的最大持有时间（单位：秒）
     * @param waitTime  加锁超时时长（单位：秒）
     * @return 是否成功获取写锁
     */
    public static boolean tryWriteLock(String lockName, int leaseTime, int waitTime) {
        RReadWriteLock rwLock = getRedissonClient().getReadWriteLock(REDIS_LOCK_PREFIX + lockName);
        RLock writeLock = rwLock.writeLock();
        try {
            boolean isLocked = writeLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (isLocked) {
                log.info("线程: {} 成功获取写锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
            } else {
                log.warn("线程: {} 未能获取写锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
            }
            return isLocked;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程: {} 尝试获取写锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), lockName, e);
            return false;
        }
    }

    /**
     * 释放写锁
     *
     * @param lockName 锁名称
     */
    public static void unlockWrite(String lockName) {
        RReadWriteLock rwLock = getRedissonClient().getReadWriteLock(REDIS_LOCK_PREFIX + lockName);
        RLock writeLock = rwLock.writeLock();
        if (writeLock.isHeldByCurrentThread()) {
            writeLock.unlock();
            log.info("线程: {} 成功释放写锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
        } else {
            log.warn("线程: {} 没有持有写锁: {}", ThreadUtil.getCurrentThreadName(), lockName);
        }
    }

    /**
     * 关闭资源
     *      在Spring应用中通常不需要手动调用此方法，由容器管理生命周期
     */
    public static void shutdown() {
        RedissonClient client = getRedissonClient();
        if (client != null) {
            client.shutdown();
            log.info("Redisson客户端已关闭");
        }
    }
}
