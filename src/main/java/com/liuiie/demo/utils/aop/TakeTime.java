package com.liuiie.demo.utils.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 耗时统计
 *
 * @author Liuiie
 * @since 2024/6/18 15:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TakeTime {
}
