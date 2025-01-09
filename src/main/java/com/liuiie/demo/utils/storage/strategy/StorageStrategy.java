package com.liuiie.demo.utils.storage.strategy;

import com.liuiie.demo.utils.storage.service.StorageService;

/**
 * 存储策略
 *
 * @author Liuiie
 * @since 2025/1/9 11:18
 */
public interface StorageStrategy {
    /**
     * 获取存储服务
     *
     * @return 存储服务
     */
    StorageService getStorageService();
}