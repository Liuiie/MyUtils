package com.liuiie.demo.utils.storage.strategy.impl;

import com.liuiie.demo.utils.storage.service.StorageService;
import com.liuiie.demo.utils.storage.service.impl.ObsStorageServiceImpl;
import com.liuiie.demo.utils.storage.strategy.StorageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ObsStorageStrategy
 *
 * @author Liuiie
 * @since 2025/1/9 11:21
 */
@Component("obsStrategy")
public class ObsStorageStrategy implements StorageStrategy {

    private final StorageService obsStorageService;

    @Autowired
    public ObsStorageStrategy(ObsStorageServiceImpl obsStorageService) {
        this.obsStorageService = obsStorageService;
    }

    @Override
    public StorageService getStorageService() {
        return obsStorageService;
    }
}
