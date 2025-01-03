package com.liuiie.demo.utils.lock;

import com.liuiie.demo.utils.common.SpringContextUtil;
import com.liuiie.demo.utils.thread.ThreadUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁工具类。
 *      该工具类提供了一系列静态方法用于在分布式环境中实现基于 Redis 的锁机制，
 *      包括加锁、自旋加锁（带超时）、解锁和自动续期功能。为了保证锁的安全性和可靠性，
 *      解锁操作只有持有相同 requestId 的客户端才能成功执行。此外，还实现了自动续期机制，
 *      确保在任务执行期间不会因为锁过期而导致其他客户端获取到锁。
 *      提供了读写锁支持，确保锁的安全性和可靠性。通过 Lua 脚本和心跳机制来保证操作的原子性和续期任务的正常终止。
 *
 * @author Liuiie
 * @since 2022/4/28 23:28
 */
@Log4j2
public class RedisLockUtil {

    /**
     * 锁键的前缀，用于标识所有由本工具类管理的锁。
     */
    public static final String KEY_PREFIX = "LOCK_KEY_";

    /**
     * 读锁后缀
     */
    public static final String READ_LOCK_SUFFIX = "_READ";

    /**
     * Spring 应用上下文中 RedisTemplate 的 bean 名称。
     */
    public static final String BEAN_STRING_REDIS_NAME = "stringRedisTemplate";

    /**
     * 存储所有正在运行的续期任务。
     */
    private static final Map<String, RenewalTask> RENEWAL_TASKS = new ConcurrentHashMap<>();

    /**
     * 使用 Holder 模式进行懒加载，确保 RedisTemplate 单例实例的线程安全初始化。
     */
    private static class RedisTemplateHolder {
        /**
         * RedisTemplate 的单例实例。
         */
        private static final StringRedisTemplate INSTANCE;

        static {
            try {
                // 从上下文中获取 RedisTemplate 实例
                INSTANCE = SpringContextUtil.getBean(BEAN_STRING_REDIS_NAME, StringRedisTemplate.class);
                if (INSTANCE == null) {
                    throw new IllegalStateException("RedisTemplate 未正确初始化");
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * 获取 RedisTemplate 实例。
     *
     * @return 返回已经初始化的 RedisTemplate 实例
     */
    private static StringRedisTemplate getRedisTemplate() {
        return RedisTemplateHolder.INSTANCE;
    }

    /**
     * 尝试获取锁，无阻塞方式。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @param expireTime   锁的有效期时间
     * @param timeUnit     锁有效期的时间单位
     * @return 如果成功获取锁，则返回 true；否则返回 false
     */
    public static boolean tryLock(String key, String requestId, long expireTime, TimeUnit timeUnit) {
        try {
            RedisTemplate<String, String> redisTemplate = getRedisTemplate();
            // 尝试设置锁，如果设置成功则启动续期任务
            Boolean result = redisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + key, requestId, expireTime, timeUnit);
            if (Boolean.TRUE.equals(result)) {
                startRenewalTask(key, requestId, expireTime, timeUnit);
            }
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("线程: {} 尝试加锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), key, e);
            return false;
        }
    }

    /**
     * 尝试获取锁，带有自旋等待且带超时。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @param expireTime   锁的有效期时间
     * @param timeUnit     锁有效期的时间单位
     * @param timeout      自旋等待的最大时间（毫秒）
     * @return 如果成功获取锁，则返回 true；否则返回 false
     */
    public static boolean tryLockWithSpin(String key, String requestId, long expireTime, TimeUnit timeUnit, long timeout) {
        long start = System.currentTimeMillis();
        try {
            RedisTemplate<String, String> redisTemplate = getRedisTemplate();
            for (;;) {
                // 尝试设置锁，如果设置成功则启动续期任务
                Boolean ret = redisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + key, requestId, expireTime, timeUnit);
                if (Boolean.TRUE.equals(ret)) {
                    startRenewalTask(key, requestId, expireTime, timeUnit);
                    return true;
                }
                // 计算已等待的时间
                long end = System.currentTimeMillis() - start;
                if (end >= timeout) {
                    return false;
                }
                // 避免 CPU 空转，适当休眠
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // 获取当前线程
            Thread currentThread = Thread.currentThread();
            String currentThreadName = currentThread.getName();
            currentThread.interrupt();
            log.warn("线程: {} 自旋加锁[{}]过程中线程被中断", currentThreadName, key, e);
            return false;
        } catch (Exception e) {
            log.error("线程: {} 尝试自旋加锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), key, e);
            return false;
        }
    }

    /**
     * 尝试释放锁，确保只有持有相同 requestId 的客户端才能解锁。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @return 如果成功解锁，则返回 true；否则返回 false
     */
    public static boolean unlock(String key, String requestId) {
        try {
            RedisTemplate<String, String> redisTemplate = getRedisTemplate();
            String lockKey = KEY_PREFIX + key;
            Object currentValue = redisTemplate.opsForValue().get(lockKey);

            if (requestId.equals(currentValue)) {
                // 停止续期任务
                stopRenewalTask(key, requestId);
                return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
            } else {
                log.warn("线程: {} 尝试解锁时发现请求ID不匹配: {} != {}", ThreadUtil.getCurrentThreadName(), requestId, currentValue);
                return false;
            }
        } catch (Exception e) {
            log.error("线程: {} 尝试解锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), key, e);
            return false;
        }
    }

    /**
     * 尝试获取读锁（共享锁），无阻塞方式，使用 Lua 脚本确保原子性。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @param expireTime   锁的有效期时间
     * @param timeUnit     锁有效期的时间单位
     * @return 如果成功获取读锁，则返回 true；否则返回 false
     */
    public static boolean readLock(String key, String requestId, long expireTime, TimeUnit timeUnit) {
        try {
            StringRedisTemplate redisTemplate = getRedisTemplate();
            // Lua 脚本：尝试增加读锁计数器，并设置过期时间（如果这是第一次获取读锁）
            String script = "local counter = redis.call('INCR', KEYS[1]);" +
                    "if counter == 1 then redis.call('PEXPIRE', KEYS[1], ARGV[1]); end;" +
                    "return counter;";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            List<String> keys = Arrays.asList(KEY_PREFIX + key + READ_LOCK_SUFFIX);
            List<String> args = Arrays.asList(String.valueOf(TimeUnit.MILLISECONDS.convert(expireTime, timeUnit)));
            Long result = redisTemplate.execute(redisScript, keys, args.toArray(new String[0]));
            if (result != null && result >= 1) {
                startRenewalTask(key + READ_LOCK_SUFFIX, requestId, expireTime, timeUnit);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("线程: {} 尝试获取读锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), key, e);
            return false;
        }
    }

    /**
     * 尝试获取写锁（独占锁），无阻塞方式。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @param expireTime   锁的有效期时间
     * @param timeUnit     锁有效期的时间单位
     * @return 如果成功获取写锁，则返回 true；否则返回 false
     */
    public static boolean writeLock(String key, String requestId, long expireTime, TimeUnit timeUnit) {
        return tryLock(key, requestId, expireTime, timeUnit);
    }

    /**
     * 尝试释放读锁（共享锁），使用 Lua 脚本确保原子性。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于查找对应的续期任务
     * @return 如果成功解锁，则返回 true；否则返回 false
     */
    public static boolean readUnlock(String key, String requestId) {
        try {
            StringRedisTemplate redisTemplate = getRedisTemplate();
            // Lua 脚本：尝试减少读锁计数器，如果计数器归零则删除锁
            String script = "local counter = redis.call('DECR', KEYS[1]);" +
                    "if counter == 0 then redis.call('DEL', KEYS[1]); end;" +
                    "return counter;";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            List<String> keys = Arrays.asList(KEY_PREFIX + key + READ_LOCK_SUFFIX);
            Long result = redisTemplate.execute(redisScript, keys);
            if (result != null && result >= 0) {
                stopRenewalTask(key + READ_LOCK_SUFFIX, requestId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("线程: {} 尝试释放读锁[{}]时发生异常", ThreadUtil.getCurrentThreadName(), key, e);
            return false;
        }
    }

    /**
     * 尝试释放写锁（独占锁）。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @return 如果成功解锁，则返回 true；否则返回 false
     */
    public static boolean writeUnlock(String key, String requestId) {
        return unlock(key, requestId);
    }

    /**
     * 启动锁续期任务。
     *      创建一个新的续期任务，并将其与 requestId 关联起来以便后续停止。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @param expireTime   锁的有效期时间
     * @param timeUnit     锁有效期的时间单位
     */
    private static void startRenewalTask(String key, String requestId, long expireTime, TimeUnit timeUnit) {
        RenewalTask task = new RenewalTask(key, requestId, expireTime, timeUnit);
        task.start();
        // 使用全局变量存储任务以便后续停止
        RENEWAL_TASKS.put(requestId, task);
    }

    /**
     * 停止锁续期任务。
     *      根据 requestId 查找并停止对应的续期任务。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于查找对应的续期任务
     */
    private static void stopRenewalTask(String key, String requestId) {
        RenewalTask task = RENEWAL_TASKS.remove(requestId);
        if (task != null) {
            task.stopRenewal();
        }
    }

    /**
     * 续期任务类。
     *      该类继承自 Thread 类，用于定期更新锁的有效期，防止锁提前过期。
     */
    private static class RenewalTask extends Thread {
        /**
         * 锁的唯一标识符。
         */
        private final String key;

        /**
         * 请求 ID，用于验证锁的所有权。
         */
        private final String requestId;

        /**
         * 锁的有效期时间。
         */
        private final long expireTime;

        /**
         * 锁有效期的时间单位。
         */
        private final TimeUnit timeUnit;

        /**
         * 控制续期任务是否继续运行的标志。
         */
        private volatile boolean running = true;

        /**
         * 构造函数。
         *      初始化续期任务所需的参数。
         *
         * @param key          锁的唯一标识符
         * @param requestId    请求 ID，用于验证锁的所有权
         * @param expireTime   锁的有效期时间
         * @param timeUnit     锁有效期的时间单位
         */
        public RenewalTask(String key, String requestId, long expireTime, TimeUnit timeUnit) {
            this.key = key;
            this.requestId = requestId;
            this.expireTime = expireTime;
            this.timeUnit = timeUnit;
        }

        /**
         * 执行续期任务。
         *      定期检查并延长锁的有效期，直到任务被显式停止。
         */
        @Override
        public void run() {
            while (running) {
                try {
                    // 定期休眠一段时间，例如锁有效期的三分之一
                    Thread.sleep(timeUnit.toMillis(expireTime) / 3);
                    extendLock(key, requestId, expireTime, timeUnit);
                } catch (InterruptedException e) {
                    Thread currentThread = Thread.currentThread();
                    String currentThreadName = currentThread.getName();
                    currentThread.interrupt();
                    log.warn("线程: {} 锁续期线程被中断", currentThreadName, e);
                    break;
                } catch (Exception e) {
                    log.error("线程: {} 锁续期过程中发生异常", ThreadUtil.getCurrentThreadName(), e);
                    break;
                }
            }
        }

        /**
         * 停止续期任务。
         */
        public void stopRenewal() {
            this.running = false;
        }
    }

    /**
     * 使用 Lua 脚本安全地延长锁的有效期。
     *      只有持有相同 requestId 的客户端能够成功延长锁的有效期。
     *
     * @param key          锁的唯一标识符
     * @param requestId    请求 ID，用于验证锁的所有权
     * @param expireTime   锁的有效期时间
     * @param timeUnit     锁有效期的时间单位
     * @return 如果成功延长锁的有效期，则返回 true；否则返回 false
     */
    private static boolean extendLock(String key, String requestId, long expireTime, TimeUnit timeUnit) {
        try {
            RedisTemplate<String, String> redisTemplate = getRedisTemplate();
            String script = "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('PEXPIRE', KEYS[1], ARGV[2]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            // 使用 Arrays.asList() 创建列表
            List<String> keys = Arrays.asList(KEY_PREFIX + key);
            List<String> args = Arrays.asList(requestId, String.valueOf(TimeUnit.MILLISECONDS.convert(expireTime, timeUnit)));
            Long result = redisTemplate.execute(redisScript, keys, args.toArray(new String[0]));
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("线程: {} 尝试延长锁时发生异常: {}", ThreadUtil.getCurrentThreadName(), key, e);
            return false;
        }
    }
}
