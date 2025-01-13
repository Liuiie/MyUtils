package com.liuiie.demo.utils.storage.context;

import com.liuiie.demo.utils.storage.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * StorageContext
 *
 * @author Liuiie
 * @since 2025/1/9 11:23
 */
@Log4j2
@Component
public class StorageContext {
    /**
     * 所有存储服务
     */
    private final Map<String, StorageService> storages;
    /**
     * 默认类型
     */
    private final String defaultType;

    private static final String STORAGE_CONTEXT_SUFFIX = "StorageService";

    @Autowired
    public StorageContext(ApplicationContext applicationContext, Environment env) {
        // 使用Spring的ApplicationContext来获取所有实现了StorageStrategy的bean
        this.storages = applicationContext.getBeansOfType(StorageService.class);
        // 从环境变量中读取默认存储类型
        // 设置minio为默认值
        this.defaultType = env.getProperty("storage.service", "minio");
    }

    /**
     * 获取默认存储服务
     *
     * @return 存储服务
     */
    public StorageService getDefaultStorageService() {
        StorageService storageService = storages.get(defaultType.toLowerCase() + STORAGE_CONTEXT_SUFFIX);
        if (storageService == null) {
            log.error("默认存储服务[{}]未找到", defaultType);
            throw new IllegalArgumentException("默认存储服务[" + defaultType + "]未找到");
        }
        return storageService;
    }

    /**
     * 根据类型获取存储服务
     *      所获取的存储服务不存在时使用默认存储服务
     *
     * @param type 类型枚举
     * @return 存储服务
     */
    public StorageService getStorageService(String type) {
        if (type == null || type.trim().isEmpty()) {
            type = defaultType;
        }
        String strategyName = type.toLowerCase() + STORAGE_CONTEXT_SUFFIX;
        StorageService storageService = storages.get(strategyName);
        if (storageService == null) {
            log.warn("存储服务[{}]未找到，尝试使用默认策略", strategyName);
            // 如果没有找到指定类型的策略，则尝试使用默认策略
            storageService = storages.get(defaultType.toLowerCase() + STORAGE_CONTEXT_SUFFIX);
            if (storageService == null) {
                log.error("不支持存储类型: {}", type);
                throw new IllegalArgumentException("不支持存储类型: " + type);
            }
        }
        return storageService;
    }
}
