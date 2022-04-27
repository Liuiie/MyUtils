package com.liuiie.demo.utils;

/**
 * 转换异常
 *
 * @author Liuiie
 * @since 2022/4/27 20:29
 */
public class ConvertException extends Exception {
    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(Throwable throwable) {
        super(throwable);
    }

    public ConvertException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
