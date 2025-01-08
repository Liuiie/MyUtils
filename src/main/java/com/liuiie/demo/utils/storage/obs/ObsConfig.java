package com.liuiie.demo.utils.storage.obs;

import com.obs.services.ObsClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 存储服务配置类
 *
 * @author Liuiie
 * @since 2025/1/6 18:18
 */
@Data
@Component
public class ObsConfig {
    @Value("${fileOperator.obs.endpoint:100.125.2.4:443}")
    private String endpoint;

    @Value("${fileOperator.obs.accessKey:0TTGO6O61405AUE0QAIK}")
    private String accessKey;

    @Value("${fileOperator.obs.secretKey:kPNgm4nfkDdgyhoK3Yv9tUo4OdtX6dWdBFdZxxcX}")
    private String secretKey;

    @Value("${fileOperator.obs.bucketName:cqkjj-guanlixitongceshi}")
    private String bucketName;

    /**
     * 获取 ObsClient
     */
    @Bean("obsClient")
    public ObsClient minioClient(){
        return new ObsClient(endpoint, accessKey, secretKey);
    }
}
