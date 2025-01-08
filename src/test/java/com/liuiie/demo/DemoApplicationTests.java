package com.liuiie.demo;

import org.junit.jupiter.api.Test;

import java.util.Scanner;
import java.util.UUID;

//@SpringBootTest
class DemoApplicationTests {
    @Test
    public void test() {

    }

    @Test
    public void scannerTest() {
        // 创建一个新的Scanner对象以读取用户输入
        Scanner scanner = new Scanner(System.in);
        // 提示用户输入地址
        System.out.println("请输入地址:");
        // 读取用户输入的一行文本
        String address = scanner.nextLine();
        // 输出用户输入的地址
        System.out.println("您输入的地址是: " + address);
        // 关闭scanner
        scanner.close();
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
