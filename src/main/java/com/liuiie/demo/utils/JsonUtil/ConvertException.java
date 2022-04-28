package com.liuiie.demo.utils.JsonUtil;

/**
 * 转换异常
 *
 * @author Liuiie
 * @since 2022/4/27 20:29
 */
public class ConvertException extends Exception {
    /**
     * 无参构造函数
     */
    public ConvertException() {
    }

    /**
     * 执行一个详细信息异常
     *
     * @param message 信息异常
     */
    public ConvertException(String message) {
        super(message);
    }

    /**
     * 用指定原因构造一个新异常
     *
     * @param cause 指定原因
     */
    public ConvertException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用详细信息与指定原因构造一个新异常
     *
     * @param message 详细信息
     * @param cause
     */
    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
