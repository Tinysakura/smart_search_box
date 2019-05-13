package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.smartsearchbox.common.command.IndexCreateCommand;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 负责初始化索引的job
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */

public class IndexInitJob implements Runnable {

    private ElkClientService elkClientService;

    private RedisClientService redisClientService;

    private String documentSetKeySuffix;

    private String behaviorSetKeySuffix;

    private LinkedBlockingQueue<IndexCreateCommand> indexCreateBlockingQueue;

    private Long waitTime;

    private static final String setKeyFormat = "%s_%s";

    /**
     *
     * @param elkClientService elk客户端
     * @param indexCreateBlockingQueue 存放索引初始化命令的阻塞队列
     * @param waitTime 从阻塞队列中取命令允许阻塞的最大时间
     */
    public IndexInitJob(ElkClientService elkClientService, RedisClientService redisClientService, LinkedBlockingQueue<IndexCreateCommand> indexCreateBlockingQueue, String documentSetKeySuffix, String behaviorSetKeySuffix, Long waitTime) {
        this.elkClientService = elkClientService;
        this.redisClientService = redisClientService;
        this.indexCreateBlockingQueue = indexCreateBlockingQueue;
        this.documentSetKeySuffix = documentSetKeySuffix;
        this.behaviorSetKeySuffix = behaviorSetKeySuffix;
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        try {
            IndexCreateCommand command = indexCreateBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS);

            if (command.getIndex() == null) {
                elkClientService.createIndex(command.getIndexName());
            } else {
                elkClientService.createIndex(command.getIndexName(), command.getIndex());
            }

            /**
             * 初始化存储文档对应的zset的key值的set
             * keyFormat : {索引名}_{文档类型set后缀}_{搜索提示字段1}_{搜索提示字段2}...
             */
            String documentSetKey = String.format(setKeyFormat, command.getIndex(), documentSetKeySuffix);

            for (String field : command.getSearchPromptFields()) {
                documentSetKey = documentSetKey.concat("_").concat(field);
            }

            if (!redisClientService.exists(documentSetKey)) {
                redisClientService.sAdd(documentSetKey);
                redisClientService.sAdd(Launch.DOCUMENT_SETS_KEYS_SET_KEY, documentSetKey);
            }

            /**
             * 初始化存储用户行为对应的zset的key值的set
             */
            String behaviorSetKey = String.format(setKeyFormat, command.getIndex(), behaviorSetKeySuffix);
            if (!redisClientService.exists(behaviorSetKey)) {
                redisClientService.sAdd(behaviorSetKey);
                redisClientService.sAdd(Launch.BEHAVIOR_SETS_KEYS_SET_KEY, behaviorSetKey);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}