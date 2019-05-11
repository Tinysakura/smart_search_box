package com.tinysakura.smartsearchbox.config;

import com.tinysakura.smartsearchbox.adapter.IkAnalyzerAdapter;
import com.tinysakura.smartsearchbox.adapter.JedisClientAdapter;
import com.tinysakura.smartsearchbox.adapter.SmartElkClientAdapter;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.prop.EndPointProp;
import com.tinysakura.smartsearchbox.prop.IndexProp;
import com.tinysakura.smartsearchbox.prop.SearchPromptProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * 使用默认的配置启动并注入Launch，如果不想使用框架提供的默认实现可以配置不扫描该包路径即可
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */
@Configuration
public class LaunchConfiguration {

    @Resource(name = "jedisPool")
    private JedisPool jedisPool;

    @Autowired
    private EndPointProp endPointProp;

    @Autowired
    private IndexProp indexInitProp;

    @Autowired
    private SearchPromptProp searchPromptProp;

    @Bean
    public Launch launch() {
        Launch launch = new Launch();

        IkAnalyzerAdapter ikAnalyzerAdapter = new IkAnalyzerAdapter(false);
        JedisClientAdapter jedisClientAdapter = new JedisClientAdapter(jedisPool, searchPromptProp.getZSetCapacity(), searchPromptProp.getZSetCacheCapacity());
        SmartElkClientAdapter smartElkClientAdapter = new SmartElkClientAdapter();

        launch.setAnalyzer(ikAnalyzerAdapter);
        launch.setRedisClient(jedisClientAdapter);
        launch.setElkClient(smartElkClientAdapter);

        return launch;
    }
}