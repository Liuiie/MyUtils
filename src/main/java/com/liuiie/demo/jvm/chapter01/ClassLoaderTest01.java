package com.liuiie.demo.jvm.chapter01;

import sun.misc.Launcher;

import java.net.URL;
import java.security.Provider;

/**
 * ClassLoaderTest01
 *
 * @author Liuiie
 * @since 2022/8/19 15:33
 */
public class ClassLoaderTest01 {
    public static void main(String[] args) {
        // 获取BootStrapClassLoader能够加载的api的路径
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        for (URL url : urls) {
            System.out.println(url.toExternalForm());
        }
        // 从上面的路径中随便找一个类来看看它的类加载器是什么：是引导类加载器
        ClassLoader classLoader = Provider.class.getClassLoader();
        System.out.println(classLoader);
    }
}
