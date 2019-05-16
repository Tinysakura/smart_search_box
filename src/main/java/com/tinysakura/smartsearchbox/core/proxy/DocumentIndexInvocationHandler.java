package com.tinysakura.smartsearchbox.core.proxy;

import com.tinysakura.smartsearchbox.common.command.DocumentAddCommand;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import com.tinysakura.smartsearchbox.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * 动态代理dao层的save方法，加入文档索引相关的逻辑
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/12
 */
@Slf4j
@SuppressWarnings("ALL")
public class DocumentIndexInvocationHandler<T> implements InvocationHandler {

    private T target;

    /**
     * 索引文档是否异步进行
     */
    private boolean async;

    /**
     * 存放索引文档指令的队列
     */
    private LinkedBlockingQueue<DocumentAddCommand> documentAddBlockingQueue;

    /**
     * 提供同步索引文档的elk客户端
     */
    private ElkClientService elkClientService;

    private RedisClientService redisClientService;

    private AnalyzerService analyzerService;

    public DocumentIndexInvocationHandler(T target, boolean async, ElkClientService elkClientService, RedisClientService redisClientService, AnalyzerService analyzerService) {
        this.target = target;
        this.async = async;
        this.elkClientService = elkClientService;
        this.redisClientService = redisClientService;
        this.analyzerService = analyzerService;
    }

    public DocumentIndexInvocationHandler(T target, boolean async, LinkedBlockingQueue<DocumentAddCommand> documentAddBlockingQueue, RedisClientService redisClientService) {
        this.target = target;
        this.async = async;
        this.documentAddBlockingQueue = documentAddBlockingQueue;
        this.redisClientService = redisClientService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        com.tinysakura.smartsearchbox.annotation.Index indexAnnotation = method.getDeclaredAnnotation(com.tinysakura.smartsearchbox.annotation.Index.class);

        if (indexAnnotation != null) {
            // TODO 使用文档索引表达式分析器判断文档是否需要被索引

            // TODO 使用搜索提示表达式分析器计算得分后存入文档对应的zset

            if (async) {
                /**
                 * 异步的情况
                 */
                try {
                    Object document = method.invoke(target, args);

                    DocumentAddCommand documentAddCommand = new DocumentAddCommand();
                    documentAddCommand.setIndex(indexAnnotation.index());
                    documentAddCommand.setDocumentType(indexAnnotation.documentType());
                    documentAddCommand.setDocument(document);
                    documentAddCommand.setSearchPromptFields(indexAnnotation.searchPromptFields());

                    try {
                        documentAddBlockingQueue.put(documentAddCommand);
                    } catch (InterruptedException e) {
                        log.info("文档索引命令存入失败", e);
                        e.printStackTrace();
                    }

                    return document;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                /**
                 * 同步的情况
                 */
                try {
                    Object document = method.invoke(target, args);

                    elkClientService.addDocument(indexAnnotation.index(), indexAnnotation.documentType(), document);

                    /**
                     * 分词后建立zset
                     */
                    String[] searchPromptFields = indexAnnotation.searchPromptFields();

                    Method[] methods = document.getClass().getDeclaredMethods();
                    Map<String, Method> methodMap = Arrays.asList(methods).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));

                    DocumentAddCommand documentAddCommand = new DocumentAddCommand();
                    documentAddCommand.setIndex(indexAnnotation.index());
                    documentAddCommand.setDocumentType(indexAnnotation.documentType());
                    documentAddCommand.setDocumentId(indexAnnotation.id());
                    documentAddCommand.setDocument(document);
                    documentAddCommand.setSearchPromptFields(indexAnnotation.searchPromptFields());

                    String documentSetKey = StringUtil.documentSetKey(documentAddCommand);
                    String behaviorSetKey = StringUtil.behaviorSetKey(documentAddCommand.getIndex());

                    for (String field : searchPromptFields) {
                        // 使用反射获取对应属性上的值
                        String methodName = "get" + StringUtil.toUpperCaseFirstOne(field);
                        try {
                            Method getMethod = methodMap.get(methodName);
                            if (method != null) {
                                Object value = getMethod.invoke(document);

                                String[] analyzerResults = analyzerService.analyzer(value.toString());

                                for (String analyzerResult : analyzerResults) {
                                    String documentZSetKey = StringUtil.documentZSetKey(documentAddCommand.getIndex(), analyzerResult);
                                    redisClientService.zAdd(documentZSetKey, value.toString(), 0d);
                                    redisClientService.sAdd(documentSetKey, documentZSetKey);
                                    redisClientService.sAdd(Launch.DOCUMENT_SETS_KEYS_SET_KEY, documentSetKey);

                                    String behaviorZSetKey = StringUtil.behaviorZSetKey(documentAddCommand.getIndex(), analyzerResult);
                                    redisClientService.zAdd(behaviorZSetKey, value.toString(), 0d);
                                    redisClientService.sAdd(behaviorSetKey, documentZSetKey);
                                    redisClientService.sAdd(Launch.BEHAVIOR_SETS_KEYS_SET_KEY, behaviorSetKey);
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                    return document;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        } else {
            /**
             * 没被注解的方法执行原来的逻辑即可
             */
            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}