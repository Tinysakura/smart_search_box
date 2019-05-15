package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.smartsearchbox.common.command.IndexCreateCommand;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import com.tinysakura.smartsearchbox.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责初始化索引的job
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */
@Slf4j
public class IndexInitJob implements Runnable {

    private ElkClientService elkClientService;

    private RedisClientService redisClientService;

    private IndexCreateCommand indexCreateCommand;

    /**
     *
     * @param elkClientService elk客户端
     * @param indexCreateCommand 索引初始化命令
     */
    public IndexInitJob(ElkClientService elkClientService, RedisClientService redisClientService, IndexCreateCommand indexCreateCommand) {
        this.elkClientService = elkClientService;
        this.redisClientService = redisClientService;
        this.indexCreateCommand = indexCreateCommand;
    }

    @Override
    public void run() {
        log.info("开始创建索引，command:{}", indexCreateCommand.toString());
        if (indexCreateCommand.getIndex() == null) {
            elkClientService.createIndex(indexCreateCommand.getIndexName());
        } else {
            elkClientService.createIndex(indexCreateCommand.getIndexName(), indexCreateCommand.getIndex());
        }

        /**
         * 初始化存储文档对应的zset的key值的set
         * keyFormat : {索引名}_{文档类型set后缀}_{搜索提示字段1}_{搜索提示字段2}...
         */
        String documentSetKey = StringUtil.documentSetKey(indexCreateCommand);

        for (String field : indexCreateCommand.getSearchPromptFields()) {
            documentSetKey = documentSetKey.concat("_").concat(field);
        }

        if (!redisClientService.exists(documentSetKey)) {
            redisClientService.sAdd(documentSetKey);
            redisClientService.sAdd(Launch.DOCUMENT_SETS_KEYS_SET_KEY, documentSetKey);
        }

        /**
         * 初始化存储用户行为对应的zset的key值的set
         */
        String behaviorSetKey = StringUtil.behaviorSetKey(indexCreateCommand.getIndexName());
        if (!redisClientService.exists(behaviorSetKey)) {
            redisClientService.sAdd(behaviorSetKey);
            redisClientService.sAdd(Launch.BEHAVIOR_SETS_KEYS_SET_KEY, behaviorSetKey);
        }
    }
}