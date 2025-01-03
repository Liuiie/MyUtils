package com.liuiie.demo.utils.thread;

/**
 * ThreadUtil
 *
 * @author Liuiie
 * @since 2025/1/3 17:23
 */
public class ThreadUtil {
    /**
     * 获取当前线程名称
     *
     * @return 当前线程名称
     */
    public static String getCurrentThreadName() {
        // 获取当前线程
        Thread currentThread = Thread.currentThread();
        return currentThread.getName();
    }
}
