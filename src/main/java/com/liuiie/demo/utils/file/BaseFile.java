package com.liuiie.demo.utils.file;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 基础文件信息
 *
 * @author Liuiie
 * @since 2025/1/15 15:40
 */
@Data
public abstract class BaseFile {
    private String type;
    private String name;
    private String path;

    /**
     * 是否是文件
     *
     * @return 是文件则返回true, 否则返回false
     */
    @JSONField(serialize = false)
    public boolean isFile() {
        return false;
    }
}
