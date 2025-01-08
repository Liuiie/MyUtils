package com.liuiie.demo.utils.storage;

import com.liuiie.demo.utils.storage.minio.MinioConfig;
import com.liuiie.demo.utils.storage.minio.MinioUtils;
import com.liuiie.demo.utils.storage.obs.ObsConfig;
import com.liuiie.demo.utils.storage.obs.ObsUtils;
import com.obs.services.ObsClient;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * StorageServiceFactory
 *
 * @author Liuiie
 * @since 2025/1/6 18:19
 */
@Component
public class StorageServiceFactory {
    /**
     * 文件存储服务配置
     */
    @Value("${fileOperator.service:minio}")
    private String service;

    /**
     * 华为云OBS客户端
     */
    @Resource(name = "obsClient")
    private ObsClient obsClient;

    /**
     * 华为云OBS配置
     */
    @Resource
    private ObsConfig obsConfig;

    /**
     * minio客户端
     */
    @Resource(name = "minioClient")
    private MinioClient minioClient;

    /**
     * minio配置
     */
    @Resource
    private MinioConfig minioConfig;

    @Bean
    public StorageService getActiveStorageService() {
        String serviceType = this.service.trim().toLowerCase();
        switch (serviceType) {
            case "obs":
                return new ObsUtils(obsClient, obsConfig);
            case "minio":
                return new MinioUtils(minioClient, minioConfig);
            default:
                throw new IllegalArgumentException("Unknown storage service: " + serviceType);
        }
    }
}
