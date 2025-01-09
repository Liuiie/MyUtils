package com.liuiie.demo.utils.storage.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhao
 * @date 2024/08/14
 */
@Data
@Configuration
public class MinioConfig {
    @Value("${fileOperator.minio.endpoint:http://172.16.115.19:9000}")
    private String endpoint;

    @Value("${fileOperator.minio.bucketName:xmgl}")
    private String bucketName;

    @Value("${fileOperator.minio.accessKey:dsep}")
    private String accessKey;

    @Value("${fileOperator.minio.secretKey:kjj@8UF8V3DIQd}")
    private String secretKey;

    /**
     * 获取 MinioClient
     */
    @Bean("minioClient")
    public MinioClient minioClient(){
        return MinioClient.builder().endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
