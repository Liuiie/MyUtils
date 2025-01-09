package com.liuiie.demo.utils.storage.strategy.impl;

import com.liuiie.demo.utils.storage.service.StorageService;
import com.liuiie.demo.utils.storage.service.impl.MinioStorageServiceImpl;
import com.liuiie.demo.utils.storage.strategy.StorageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MinioStorageStrategy
 *
 * @author Liuiie
 * @since 2025/1/9 11:20
 */
@Component("minioStrategy")
public class MinioStorageStrategy implements StorageStrategy {

    private final StorageService minioStorageService;

    @Autowired
    public MinioStorageStrategy(MinioStorageServiceImpl minioStorageService) {
        this.minioStorageService = minioStorageService;
    }

    @Override
    public StorageService getStorageService() {
        return minioStorageService;
    }
}