package com.liuiie.demo.utils.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedissionConfig
 *
 * @author Liuiie
 * @since 2025/1/2 17:58
 */
@Configuration
public class RedissionConfig {

    @Value(value = "${spring.redis.host}")
    private String host;

    @Value(value = "${spring.redis.port}")
    private Integer port;

    @Value(value = "${spring.redis.database}")
    private Integer database;

    @Value(value = "${spring.redis.password}")
    private String password;

    @Value(value = "${spring.redis.timeout}")
    private Integer timeout;

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        String address = "redis://" + host + ":" + port;
        singleServerConfig.setAddress(address);
        singleServerConfig.setDatabase(database);
        singleServerConfig.setTimeout(timeout);
        // singleServerConfig.setPassword(password);
        return Redisson.create(config);
    }

}
