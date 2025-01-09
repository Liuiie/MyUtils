package com.liuiie.demo.utils.common;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类
 * 在非spring生命周期的地方使用javabean
 *
 * @author Liuiie
 * @since 2022/4/28 22:52
 */
@SuppressWarnings("unchecked")
@Component
public class SpringContextUtil implements BeanFactoryPostProcessor, ApplicationContextAware {
    /**
     * Spring应用上下文环境
     */
    private static ApplicationContext appContext;
    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (SpringContextUtil.beanFactory == null) {
            SpringContextUtil.beanFactory = beanFactory;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtil.appContext == null) {
            SpringContextUtil.appContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  类名
     * @param clazz 类型
     * @param <T>   泛型
     * @return 类型实体
     * @throws BeansException 实例化异常
     */
    public static <T> T getBean(String name, Class<T> clazz) throws BeansException {
        return appContext.getBean(name, clazz);
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 类名
     * @return 类实体
     */
    public static <T> T getBean(String name) {
        return (T) appContext.getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return 类实体
     */
    public static <T> T getBean(Class<T> clazz) {
        return appContext.getBean(clazz);
    }


    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name 实例名称
     * @return 包含与所给名称匹配的bean定义，则返回true，否则返回false
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name 实例名称
     * @return 给定名字的bean是singleton，则返回true，否则返回false
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * 获取给定名称注册的实例的类型
     *
     * @param name 实例名称
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name 实例名称
     * @return 给定的bean名字在bean定义中的所有别名
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     *
     * @param invoker 查询对象
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles() {
        return appContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return activeProfiles.length != 0 ? activeProfiles[0] : null;
    }

    /**
     * 获取配置文件中的值
     *
     * @param key 配置文件的key
     * @return 当前的配置文件的值
     */
    public static String getRequiredProperty(String key) {
        return appContext.getEnvironment().getRequiredProperty(key);
    }
}
