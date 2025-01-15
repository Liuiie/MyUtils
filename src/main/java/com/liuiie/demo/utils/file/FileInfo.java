package com.liuiie.demo.utils.file;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.File;

/**
 * 文件信息
 *
 * @author Liuiie
 * @since 2025/1/15 15:41
 */
public class FileInfo extends BaseFile{
    public FileInfo(String name, String path) {
        File file = new File(path);
        this.setType("File");
        this.setName(name);
        this.setPath(path);
    }

    @JSONField(serialize = false)
    @Override
    public boolean isFile() {
        return true;
    }
}
