package com.tinysakura.smartsearchbox.core;

import com.tinysakura.smartsearchbox.common.command.DocumentAddCommand;
import com.tinysakura.smartsearchbox.common.command.IndexCreateCommand;
import com.tinysakura.smartsearchbox.prop.EndPointProp;
import com.tinysakura.smartsearchbox.prop.IndexProp;
import com.tinysakura.smartsearchbox.prop.SearchPromptProp;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
 * 8.开启定时任务管理文档对应zset与用户行为对应zset
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */

public class Launch implements ApplicationContextAware, BeanPostProcessor {
    /**
     * data
     */
    private EndPointProp endPointProp;

    private IndexProp indexProp;

    private SearchPromptProp searchPromptProp;

    /**
     * component
     */
    private ApplicationContext applicationContext;

    /**
     * 分词能力组件
     */
    private AnalyzerService analyzerService;

    /**
     * 索引能力组件
     */
    private ElkClientService elkClientService;

    /**
     * redis交互能力组件
     */
    private RedisClientService redisClientService;

    /**
     * 用来存放索引文档指令的阻塞队列
     */
    private LinkedBlockingQueue<DocumentAddCommand> documentAddBlockingQueue;

    /**
     * 用来存放创建索引指令的阻塞队列，索引初始化完成后销毁
     */
    private LinkedBlockingQueue<IndexCreateCommand> indexCreateBlockingQueue;

    /**
     * 初始化索引线程池，索引初始化完成后销毁
     */
    private ExecutorService indexInitThreadPool;

    /**
     * 执行索引文档任务的线程池
     */
    private ExecutorService documentIndexThreadPool;

    public Launch() {
        this.documentAddBlockingQueue = new LinkedBlockingQueue<>();
        this.indexCreateBlockingQueue = new LinkedBlockingQueue<>();
        this.indexInitThreadPool = Executors.newFixedThreadPool(this.indexProp.getIndexInitThreadPoolSize());
        this.documentIndexThreadPool = Executors.newFixedThreadPool(this.indexProp.getDocumentIndexThreadPoolSize());
    }

    public void setAnalyzer(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    public void setElkClient(ElkClientService elkClientService) {
        this.elkClientService = elkClientService;
    }

    public void setRedisClient(RedisClientService redisClientService) {
        this.redisClientService = redisClientService;
    }

    public void setEndPointProp(EndPointProp endPointProp) {
        this.endPointProp = endPointProp;
    }

    public void setIndexProp(IndexProp indexProp) {
        this.indexProp = indexProp;
    }

    public void setSearchPromptProp(SearchPromptProp searchPromptProp) {
        this.searchPromptProp = searchPromptProp;
    }

    /**
     * 初始化文档对应的zset
     */
    private void initDocumentZSet() {
        if (!redisClientService.exists(searchPromptProp.getDocumentZSetKey())) {
            redisClientService.zSet(searchPromptProp.getDocumentZSetKey(), null);
        }
    }

    /**
     * 初始化用户行为对应的zset
     */
    private void initUserBehaviorZSet() {
        if (!redisClientService.exists(searchPromptProp.getBehaviorZSetKey())) {
            redisClientService.zSet(searchPromptProp.getBehaviorZSetKey(), null);
        }
    }

    /**
     * 处理@Document注解
     */
    private void documentAnnotationProcessor() {

    }

    /**
     * 处理@Field注解
     */
    private void fieldAnnotationProcessor() {

    }

    /**
     * 处理@Index注解
     */
    private void indexAnnotationProcessor() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * bean初始化完成后的回调
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}