package com.liuiie.demo.utils.storage;

import lombok.Getter;

/**
 * 存储类型枚举
 *
 * @author Liuiie
 * @since 2025/1/8 10:49
 */
@Getter
public enum StorageTypeEnum {
    /**
     * type: obs, describe: 华为云
     */
    OBS("obs", "华为云"),
    /**
     * type: aliYun, describe: 阿里云
     */
    ALIYUN("aliYun", "阿里云"),
    /**
     * type: tencentCloud, describe: 腾讯云
     */
    TENCENT_CLOUD("tencentCloud", "腾讯云"),
    /**
     * type: minio, describe: Minio
     */
    MINIO("minio", "Minio"),
    /**
     * type: local, describe: 本地
     */
    LOCAL("local", "本地");

    /**
     * 类型
     */
    private final String type;
    /**
     * 描述
     */
    private final String describe;

    /**
     * 构造函数
     *
     * @param type 类型
     * @param describe 描述
     */
    StorageTypeEnum(String type, String describe) {
        this.type = type;
        this.describe = describe;
    }

}
