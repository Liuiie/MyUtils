package com.liuiie.demo.utils.lock;

import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * RedissionController
 *
 * @author Liuiie
 * @since 2025/1/3 9:29
 */
@Log4j2
@RestController
@RequestMapping("/redission")
public class RedissionController {

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    private static final String LOCK = "LOCK_";

    public RedissionController(RedissonClient redissonClient, StringRedisTemplate stringRedisTemplate) {
        this.redissonClient = redissonClient;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("tryLock")
    public String tyLock() throws InterruptedException {
        String key = UUID.randomUUID().toString();
        RedissonLockUtil.tryLock(key, 30, 30);
        try {
            log.info("{} ", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Thread.sleep(8000);
        } finally {
            // 3.解锁
            RedissonLockUtil.unlock(key);
            log.info("{} ", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        }
        return key;
    }

    @GetMapping("/getString")
    public String getString() throws InterruptedException {
        // 1.获取一把锁，只要锁的名字一样，就是同一把锁
        RLock rLock = redissonClient.getLock(LOCK);
        // 2.加锁
        rLock.lock(); // 阻塞式等待
        try {
            log.info("{} ", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            log.info("{}：获得锁", Thread.currentThread().getName());
            Thread.sleep(8000);
        } finally {
            // 3.解锁
            rLock.unlock();
            log.info("{} ", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            log.info("{}：释放锁", Thread.currentThread().getName());
        }

        return "聂可以";
    }

    @GetMapping("/read")
    public String readValue() throws Exception {
        RReadWriteLock myLock = redissonClient.getReadWriteLock("readLock");
        RLock rLock = myLock.readLock();
        rLock.lock();
        String writeValue = "";
        try {
            log.info("读锁加锁成功...{}", Thread.currentThread().getId());
            writeValue = stringRedisTemplate.opsForValue().get("writeValue");
            TimeUnit.SECONDS.sleep(8);
        } finally {
            log.info("读锁解锁成功...{}", Thread.currentThread().getId());
            rLock.unlock();
        }

        return writeValue;
    }

    @GetMapping("/write")
    public String writeValue() throws Exception {
        RReadWriteLock myLock = redissonClient.getReadWriteLock("writeLock");
        RLock rLock = myLock.writeLock();
        rLock.lock();
        String writeValue = "";
        try {
            log.info("写锁加锁成功...{}", Thread.currentThread().getId());
            writeValue = UUID.randomUUID().toString();
            TimeUnit.SECONDS.sleep(8);
            stringRedisTemplate.opsForValue().set("writeValue", writeValue);
        } finally {
            log.info("写锁解锁成功...{}", Thread.currentThread().getId());
            rLock.unlock();
        }

        return writeValue;
    }

}
