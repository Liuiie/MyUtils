package com.liuiie.demo.utils.CurrentThread;

/**
 * 本地线程变量存放类
 *
 * @author Liuiie
 * @since 2022/4/27 21:34
 */
public class CurrentThread {
    private static ThreadLocal<ThreadInfo> threadLocalThreadInfo = new ThreadLocal<>();

    private static ThreadLocal<Integer> threadLocalThreadInt = new ThreadLocal<>();

    public static void setThreadInfo(ThreadInfo threadInfo) {
        if (threadInfo == null) {
            threadLocalThreadInfo.remove();
        } else {
            threadLocalThreadInfo.set(threadInfo);
        }
    }

    public static ThreadInfo getThreadInfo() {
        return threadLocalThreadInfo.get();
    }

    public static String getThreadName() {
        String ret = "";
        ThreadInfo threadInfo = threadLocalThreadInfo.get();
        if (threadInfo != null) {
            ret = threadInfo.getName();
        }
        if (ret == null || ret.isEmpty()) {
            ret = "unknown";
        }
        return ret;
    }

    public static String getThreadMessage() {
        String ret = "";
        ThreadInfo threadInfo = threadLocalThreadInfo.get();
        if (threadInfo != null) {
            ret = threadInfo.getMessage();
        }
        if (ret == null || ret.isEmpty()) {
            ret = "unknown";
        }
        return ret;
    }

    public static void setThreadLocalThreadInt(final Integer i) {
        if (i == null) {
            threadLocalThreadInt.remove();
        } else {
            threadLocalThreadInt.set(i);
        }
    }

    public static Integer getThreadLocalThreadInt() {
        return threadLocalThreadInt.get();
    }

    /**
     * 全面清理本地线程缓存
     */
    public static void clearThreadLocal() {
        threadLocalThreadInfo.remove();
        threadLocalThreadInt.remove();
    }
}
