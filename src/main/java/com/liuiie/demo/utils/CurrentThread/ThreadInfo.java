package com.liuiie.demo.utils.CurrentThread;

/**
 * ThreadInfo
 *
 * @author Liuiie
 * @since 2022/4/28 22:36
 */
public class ThreadInfo {
    private String name;

    private String message;

    public ThreadInfo() {
    }

    public ThreadInfo(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
