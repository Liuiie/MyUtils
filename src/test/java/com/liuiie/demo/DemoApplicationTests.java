package com.liuiie.demo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

//@SpringBootTest
class DemoApplicationTests {
    @Test
    public void test() {

    }

    @Test
    public void createUUID() {
        for (int i = 0; i < 6; i++) {
            System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
        }
    }

    public static String removeNumberAndHash(String input) {
        // 使用正则表达式匹配并替换#及其前面的数字
        return input.replaceAll("\\d+#", "");
    }
}
