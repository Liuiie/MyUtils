package com.liuiie.demo.utils;

/**
 * currentThread
 *
 * @author Liuiie
 * @since 2022/4/27 21:34
 */
public class CurrentThread {
    private static ThreadLocal<String> threadName = new ThreadLocal<>();

    public static void setThreadName(String threadName) {

    }

    public static String getThreadName() {
        return null;
    }
}
