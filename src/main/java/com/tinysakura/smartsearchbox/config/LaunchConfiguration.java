package com.tinysakura.smartsearchbox.config;

import com.tinysakura.smartsearchbox.adapter.IkAnalyzerAdapter;
import com.tinysakura.smartsearchbox.adapter.JedisClientAdapter;
import com.tinysakura.smartsearchbox.adapter.SmartElkClientAdapter;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.config.prop.EndPointProp;
import com.tinysakura.smartsearchbox.config.prop.IndexProp;
import com.tinysakura.smartsearchbox.config.prop.SearchPromptProp;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * 使用默认的配置启动并注入Launch，如果不想使用框架提供的默认实现可以配置扫描该包路径时忽略该config类即可
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
    private IndexProp indexProp;

    @Autowired
    private SearchPromptProp searchPromptProp;

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public Launch launch() {
        Launch launch = new Launch();

        AnalyzerService ikAnalyzerAdapter = ikAnalyzerAdapter();
        RedisClientService jedisClientAdapter = jedisClientAdapter();
        ElkClientService smartElkClientAdapter = smartElkClientAdapter();

        launch.setAnalyzer(ikAnalyzerAdapter);
        launch.setRedisClient(jedisClientAdapter);
        launch.setElkClient(smartElkClientAdapter);
        launch.setEndPointProp(endPointProp);
        launch.setIndexProp(indexProp);
        launch.setSearchPromptProp(searchPromptProp);
        launch.setRedissonClient(redissonClient);

        return launch;
    }

    @Bean
    public AnalyzerService ikAnalyzerAdapter() {
        IkAnalyzerAdapter ikAnalyzerAdapter = new IkAnalyzerAdapter(false);

        return ikAnalyzerAdapter;
    }

    @Bean
    public RedisClientService jedisClientAdapter() {
        JedisClientAdapter jedisClientAdapter = new JedisClientAdapter(jedisPool, searchPromptProp.getZSetCapacity(), searchPromptProp.getZSetCacheCapacity());

        return jedisClientAdapter;
    }

    @Bean
    public ElkClientService smartElkClientAdapter() {
        ElkClientService smartElkClientAdapter = new SmartElkClientAdapter();

        return smartElkClientAdapter;
    }

}