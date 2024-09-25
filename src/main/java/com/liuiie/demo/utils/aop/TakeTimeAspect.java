package com.liuiie.demo.utils.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 耗时统计
 *
 * @author Liuiie
 * @since 2024/6/18 15:30
 */
@Slf4j
@Aspect
@Component
public class TakeTimeAspect {
    // 切点
    // @Pointcut("execution(* com.liuiie.demo.utils..*(..)) && !@annotation(com.liuiie.demo.utils.aop.TakeTime)")
    @Pointcut("@annotation(com.liuiie.demo.utils.aop.TakeTime)")
    public void aspect() {
    }

    @Around("aspect()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        // 执行切点
        Object result = proceedingJoinPoint.proceed();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        log.info("请求方法: {}#{}, 请求耗时 ：{} ms", signature.getDeclaringTypeName(), signature.getName(), System.currentTimeMillis() - start);
        log.info("========================== end ==========================" + System.lineSeparator());
        return result;
    }
}
