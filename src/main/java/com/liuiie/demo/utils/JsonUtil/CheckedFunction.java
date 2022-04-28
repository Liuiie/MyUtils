package com.liuiie.demo.utils.JsonUtil;

import org.springframework.lang.Nullable;

/**
 * 受检函数
 *
 * @param <T>
 * @param <R>
 * @author lirui
 * @since 2022-3-22
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    /**
     * 应用
     *
     * @param t 泛型入参
     * @return 结果
     * @throws Exception 异常
     */
    @Nullable
    R apply(@Nullable T t) throws Exception;
}
