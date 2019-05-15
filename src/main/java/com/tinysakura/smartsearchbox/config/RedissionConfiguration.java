package com.tinysakura.smartsearchbox.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/14
 */
@Configuration
public class RedissionConfiguration {
    @Value("${jedis.pool.host}")
    private String host;

    @Value("${jedis.pool.port}")
    private String port;

    @Bean
    public RedissonClient getRedisson(){

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        // 添加主从配置
        // config.useMasterSlaveServers().setMasterAddress("").setPassword("").addSlaveAddress(new String[]{"",""});

        return Redisson.create(config);
    }
}