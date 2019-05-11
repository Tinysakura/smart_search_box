package com.tinysakura.smartsearchbox.core;

import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ELKClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;

/**
 * 框架启动类，随着spring ioc容器的创建而启动
 * 主要任务：
 * 1.根据配置创建elastic search java客户端
 * 2.根据配置创建redis java客户端
 * 3.在redis中创建文档对应搜索提示索引的数据结构
 * 4.在redis中创建用户行为对应搜索提示索引的数据结构
 * 5.读取配置到相关配置信息bean类中
 * 6.根据配置扫描指定包下的@Document和@Field注解，根据注解信息异步创建索引
 * 7.根据配置扫描指定包下的@Index，使用动态代理代理dao层save方法的行为
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */

public class Launch {
    /**
     * 分词能力
     */
    private AnalyzerService analyzerService;

    /**
     * 索引能力
     */
    private ELKClientService elkClientService;

    /**
     * redis交互能力
     */
    private RedisClientService redisClientService;

    public Launch() {

    }

    public void setAnalyzer(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    public void setElkClient(ELKClientService elkClientService) {
        this.elkClientService = elkClientService;
    }

    public void setRedisClient(RedisClientService redisClientService) {
        this.redisClientService = redisClientService;
    }
}