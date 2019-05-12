package com.tinysakura.smartsearchbox.core;

import com.tinysakura.core.document.DocumentType;
import com.tinysakura.core.document.Properties;
import com.tinysakura.core.index.Index;
import com.tinysakura.smartsearchbox.annotation.Document;
import com.tinysakura.smartsearchbox.common.command.DocumentAddCommand;
import com.tinysakura.smartsearchbox.common.command.IndexCreateCommand;
import com.tinysakura.smartsearchbox.core.proxy.DocumentIndexInvocationHandler;
import com.tinysakura.smartsearchbox.prop.EndPointProp;
import com.tinysakura.smartsearchbox.prop.IndexProp;
import com.tinysakura.smartsearchbox.prop.SearchPromptProp;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import com.tinysakura.smartsearchbox.util.ReflectUtil;
import com.tinysakura.smartsearchbox.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
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
@Slf4j
public class Launch implements ApplicationContextAware, BeanPostProcessor {
    /**
     * data
     */
    private EndPointProp endPointProp;

    private IndexProp indexProp;

    private SearchPromptProp searchPromptProp;

    private Set<Class<?>> beanClassesSet;

    private Set<Class<?>> daoClassesSet;

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
        this.beanClassesSet = ReflectUtil.getClasses(indexProp.getDocumentAnnotationScanPath(), false);

        for (Class clazz : beanClassesSet) {
            Document documentAnnotation = (Document) clazz.getDeclaredAnnotation(Document.class);
            IndexCreateCommand indexCreateCommand = new IndexCreateCommand();

            indexCreateCommand.setIndexName(documentAnnotation.indexName());
            Index.Builder indexBuilder = new Index.Builder();
            indexBuilder.shardsNumber(documentAnnotation.shardsNumber()).replicasNumber(documentAnnotation.replicasNumber());

            DocumentType.Builder documentTypeBuilder = null;

            if (!StringUtils.isEmpty(documentAnnotation.documentType())) {
                documentTypeBuilder = new DocumentType.Builder();
                /**
                 * 用附加的内部信息扩展索引结构
                 */
                documentTypeBuilder.dynamic(documentAnnotation.dynamic()).extraAll(documentAnnotation.extraAll())
                        .extraIndex(documentAnnotation.extraIndex()).extraSize(documentAnnotation.extraSize())
                        .extraSoure(documentAnnotation.extraSource()).extraTimestamp(documentAnnotation.extraTimestamp())
                        .extraType(documentAnnotation.extraType());

                if (!StringUtils.isEmpty(documentAnnotation.ttl())) {
                    documentTypeBuilder.extraTTL(documentAnnotation.ttl());
                }

                if (!StringUtils.isEmpty(documentAnnotation.id())) {
                    documentTypeBuilder.extraId(documentAnnotation.id());
                }

                /**
                 * 文档类型映射
                 */
                fieldAnnotationProcessor(clazz, documentTypeBuilder);
            }

            if (documentTypeBuilder != null) {
                indexBuilder.mapping(documentTypeBuilder.build());
            }

            indexCreateCommand.setIndex(indexBuilder.build().getIndex());

            try {
                indexCreateBlockingQueue.put(indexCreateCommand);
            } catch (InterruptedException e) {
                log.info("索引创建命令存入失败", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理@Field注解
     */
    private void fieldAnnotationProcessor(Class clazz, DocumentType.Builder documentTypeBuilder) {
        Field[] declaredFields = clazz.getDeclaredFields();
        Properties.Builder propertiesBuilder = null;

        for (Field field : declaredFields) {
            com.tinysakura.smartsearchbox.annotation.Field fieldAnnotation = field.getDeclaredAnnotation(com.tinysakura.smartsearchbox.annotation.Field.class);

            if (fieldAnnotation != null) {
                if (propertiesBuilder == null) {
                    propertiesBuilder = new Properties.Builder();
                }

                propertiesBuilder.type(fieldAnnotation.type()).boost(fieldAnnotation.boost());

                if (!StringUtils.isEmpty(fieldAnnotation.fieldName())) {
                    propertiesBuilder.name(fieldAnnotation.fieldName());
                } else {
                    /**
                     * 使用默认属性名
                     */
                    propertiesBuilder.name(field.getName());
                }

                if (!StringUtils.isEmpty(fieldAnnotation.analyzer())) {
                    propertiesBuilder.analyzer(fieldAnnotation.fieldName());
                }

                if (!StringUtils.isEmpty(fieldAnnotation.dateFormat())) {
                    propertiesBuilder.format(fieldAnnotation.dateFormat());
                }
            }
        }

        if (propertiesBuilder != null) {
            documentTypeBuilder.properties(propertiesBuilder.build());
        }
    }

    /**
     * 处理@Index注解
     */
    private void indexAnnotationProcessor() {
        this.daoClassesSet = ReflectUtil.getClasses(indexProp.getIndexAnnotationScanPath(), false);

        for (Class clazz : daoClassesSet) {
            Method[] declaredMethods = clazz.getDeclaredMethods();

            for (Method method : declaredMethods) {
                com.tinysakura.smartsearchbox.annotation.Index indexAnnotation = method.getDeclaredAnnotation(com.tinysakura.smartsearchbox.annotation.Index.class);

                if (indexAnnotation != null) {
                    /**
                     * 使用动态代理加入文档索引的逻辑
                     */
                    Object target = this.applicationContext.getBean(clazz);
                    DocumentIndexInvocationHandler invocationHandler;

                    if (indexProp.isAsync()) {
                        invocationHandler = new DocumentIndexInvocationHandler(target, indexProp.isAsync(), documentAddBlockingQueue, redisClientService);
                    } else {
                        invocationHandler = new DocumentIndexInvocationHandler(target, indexProp.isAsync(), elkClientService, redisClientService);
                    }

                    Object proxyInstance = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), invocationHandler);
                    /**
                     * 删除旧的bean使用被修饰过的bean进行替换
                     */
                    dynamicInjectBean(proxyInstance, target.getClass(), indexAnnotation.beanName());
                }
            }
        }

    }

    /**
     * 向ioc容器动态注入bean
     * @param bean
     */
    private void dynamicInjectBean(Object bean, Class clazz, String beanName) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        if (StringUtils.isEmpty(beanName)) {
            /**
             * bean name为空时默认使用类名开头转小写作为bean name
             */
            beanName = StringUtil.toLowerCaseFirstOne(clazz.getSimpleName());
        }

        beanFactory.removeBeanDefinition(beanName);
        applicationContext.getAutowireCapableBeanFactory().applyBeanPostProcessorsAfterInitialization(bean, clazz.getName());
        beanFactory.registerSingleton(clazz.getName(), bean);
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