package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.smartsearchbox.common.command.DocumentAddCommand;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import com.tinysakura.smartsearchbox.util.StringUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 负责索引文档的job
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */
@SuppressWarnings("ALL")
public class DocumentIndexJob implements Runnable {
    private ElkClientService elkClientService;

    private DocumentAddCommand documentAddCommand;

    private RedisClientService redisClientService;

    private AnalyzerService analyzerService;

    /**
     *
     * @param elkClientService elk客户端
     * @param documentAddCommand 索引文档命令
     */
    public DocumentIndexJob(ElkClientService elkClientService, RedisClientService redisClientService, AnalyzerService analyzerService, DocumentAddCommand documentAddCommand) {
        this.elkClientService = elkClientService;
        this.documentAddCommand = documentAddCommand;
        this.redisClientService = redisClientService;
        this.analyzerService = analyzerService;
    }

    @Override
    public void run() {
        Object document = documentAddCommand.getDocument();
        String[] searchPromptFields = documentAddCommand.getSearchPromptFields();

        Method[] methods = document.getClass().getDeclaredMethods();
        Map<String, Method> methodMap = Arrays.asList(methods).stream().collect(Collectors.toMap(e -> e.getName(), e -> e));

        /**
         * 索引文档
         */
        if (!StringUtils.isEmpty(documentAddCommand.getDocumentId())) {
            /**
             * 使用反射获取到id字段上的值
             */
            String methodName = "get" + StringUtil.toUpperCaseFirstOne(documentAddCommand.getDocumentId());
            try {
                Method method = methodMap.get(methodName);
                if (method != null) {
                    String documentId = method.invoke(documentAddCommand.getDocument()).toString();
                    elkClientService.addDocument(documentAddCommand.getIndex(), documentAddCommand.getDocumentType(), documentId, documentAddCommand.getDocument());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            elkClientService.addDocument(documentAddCommand.getIndex(), documentAddCommand.getDocumentType(), documentAddCommand.getDocument());
        }

        /**
         * 分词后建立zset
         */
        String documentSetKey = StringUtil.documentSetKey(documentAddCommand);
        String behaviorSetKey = StringUtil.behaviorSetKey(documentAddCommand.getIndex());

        for (String field : searchPromptFields) {
            // 使用反射获取对应属性上的值
            String methodName = "get" + StringUtil.toUpperCaseFirstOne(field);
            try {
                Method method = methodMap.get(methodName);
                if (method != null) {
                    Object value = method.invoke(document);

                    String[] analyzerResults = analyzerService.analyzer(value.toString());

                    for (String analyzerResult : analyzerResults) {
                        // 将结果填入对应的zset, 默认的初始score都为0
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
    }
}