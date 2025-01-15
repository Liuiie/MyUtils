package com.liuiie.demo.utils.file;

import java.io.File;

/**
 * FileUtil
 * 
 * @author Liuiie
 * @since 2025/1/15 16:40
 */public class FileUtil {

    /**
     * 文件统计
     *
     * @param file 文件对象
     * @return 文件信息实例
     */
    public static BaseFile fileCount(File file) {
        // 判断是否为文件，如果是文件则直接执行操作并退出
        if (file.isFile()) {
            return new FileInfo(file.getName(), file.getAbsolutePath());
        }
        // 构建初始文件夹
        DirectoryInfo dir = new DirectoryInfo(file.getName(), file.getAbsolutePath());
        // 获取文件夹下所有文件
        File[] files = file.listFiles();
        if (files == null) {
            return dir;
        }
        // 处理文件夹下所有文件
        for (File child : files) {
            BaseFile baseFile = fileCount(child);
            if (baseFile.isFile()) {
                dir.countFile();
            } else {
                dir.countDir();
            }
            dir.addChild(baseFile);
        }
        return dir;
    }
}
