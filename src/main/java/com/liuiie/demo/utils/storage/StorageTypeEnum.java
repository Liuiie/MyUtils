package com.liuiie.demo.utils.storage;

/**
 * StorageTypeEnum
 *
 * @author Liuiie
 * @since 2025/1/8 10:49
 */
public enum StorageTypeEnum {
    /**
     * name: 华为云, type: 1
     */
    OBS("华为云", 1),
    /**
     * name: 阿里云, type: 2
     */
    ALIYUN("阿里云", 2),
    /**
     * name: 腾讯云, type: 3
     */
    TENCENT_CLOUD("腾讯云", 3),
    /**
     * name: Minio, type: 4
     */
    MINIO("Minio", 4),
    /**
     * name: 本地, type: 5
     */
    LOCAL("本地", 5);

    private String name;
    private Integer type;

    StorageTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
