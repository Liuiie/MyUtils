package com.liuiie.demo;

import com.alibaba.fastjson.JSON;
import com.liuiie.demo.utils.file.BaseFile;
import com.liuiie.demo.utils.file.DirectoryInfo;
import com.liuiie.demo.utils.file.FileInfo;
import com.liuiie.demo.utils.file.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

//@SpringBootTest
class DemoApplicationTests {
    @Test
    public void test() {

    }

    @Test
    public void fileTest() {
        File file = new File("G:\\Temporary\\新建文件夹");
        BaseFile baseFile = FileUtil.fileCount(file);
        if (baseFile instanceof DirectoryInfo) {
            DirectoryInfo dir = (DirectoryInfo) baseFile;
            List<FileInfo> fileInfos = dir.collectFileInfo();
            System.out.println(JSON.toJSONString(fileInfos));
        }
        System.out.println(JSON.toJSONString(baseFile));
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
