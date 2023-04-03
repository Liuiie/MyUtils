package com.liuiie.demo.utils.lock;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类
 *      在非spring生命周期的地方使用javabean
 *
 * @author Liuiie
 * @since 2022/4/28 22:52
 */
@SuppressWarnings("unchecked")
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name 类名
     * @param clazz 类型
     * @return 类型实体
     * @param <T> 泛型
     * @throws BeansException 实例化异常
     */
    public static <T> T getBean(String name, Class<T> clazz) throws BeansException {
        return (T) appContext.getBean(name);
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 类名
     * @return 类实体
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz 类型
     * @return 类实体
     * @param <T> 泛型
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
}
