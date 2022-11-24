package com.liuiie.demo.jvm.chapter01;

/**
 * ClassLoaderTest
 *
 * @author Liuiie
 * @since 2022/8/19 14:11
 */
public class ClassLoaderTest {
    public static void main(String[] args) {
        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        // sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(systemClassLoader);

        // 获取其上层：扩展类加载器
        ClassLoader extensionClassLoader = systemClassLoader.getParent();
        // sun.misc.Launcher$ExtClassLoader@13221655
        System.out.println(extensionClassLoader);

        // 试图获取其上层：获取不到引导类加载器
        ClassLoader bootstrapClassLoader = extensionClassLoader.getParent();
        // null
        System.out.println(bootstrapClassLoader);

        // 对于用户自定义类来说：默认使用系统类加载器进行加载
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        // sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println(classLoader);

        // String类使用引导类加载器进行加载的。---> Java的核心类库都是使用引导类加载器进行加载的。
        ClassLoader stringClassLoader = String.class.getClassLoader();
        // null
        System.out.println(stringClassLoader);
    }
}
