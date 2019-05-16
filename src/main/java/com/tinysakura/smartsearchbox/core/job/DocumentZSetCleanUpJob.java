package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.bean.query.result.Hit;
import com.tinysakura.bean.query.result.QueryResponse;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import com.tinysakura.smartsearchbox.util.StringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 负责维护文档相关的zSet的job
 * 任务较重，分布式环境下需要保证幂等，应当使用定时任务的形式在访问低峰期进行
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */
public class DocumentZSetCleanUpJob implements Runnable {

    private static final String DOCUMENT_ZSET_CLEANUP_JOB = "document_zset_cleanup_key";

    private ElkClientService elkClientService;

    private RedisClientService redisClientService;

    private Long zSetCapacity;

    private Long zSetCacheCapacity;

    private String analyzer;

    private RedissonClient redissonClient;

    public DocumentZSetCleanUpJob(ElkClientService elkClientService, RedisClientService redisClientService, Long zSetCapacity, Long zSetCacheCapacity, String analyzer, RedissonClient redissonClient) {
        this.elkClientService = elkClientService;
        this.redisClientService = redisClientService;
        this.zSetCapacity = zSetCapacity;
        this.zSetCacheCapacity = zSetCacheCapacity;
        this.analyzer = analyzer;
        this.redissonClient = redissonClient;
    }

    @Override
    public void run() {
        /**
         * 使用分布式锁保证幂等
         */
        RLock rLock = redissonClient.getLock(DOCUMENT_ZSET_CLEANUP_JOB);

        boolean lock = rLock.tryLock();
        if (lock) {
            try {
                /**
                 * 从三级存储结构的最上层取出所有文档相关的set keys
                 * keyFormat : {索引名}_{文档类型set后缀}_{搜索提示字段1}_{搜索提示字段2}...
                 */
                Set<String> documentSetKeys = redisClientService.sMembers(Launch.DOCUMENT_SETS_KEYS_SET_KEY);

                for (String documentSetKey : documentSetKeys) {
                    String index = StringUtil.extractIndexNameFromKey(documentSetKey);
                    String[] fields = StringUtil.extractFieldsFromKey(documentSetKey);

                    /**
                     * 根据文档得分重排序zset
                     */
                    Set<String> zSetKeys = redisClientService.sMembers(documentSetKey);

                    for (String zSetKey : zSetKeys) {
                        redisClientService.del(zSetKey);
                        String keyWord = StringUtil.extractKeywordFromDocumentZSetKey(zSetKey);

                        Map<String, Object> fieldMap = new HashMap<>(fields.length);

                        for (String field : fields) {
                            fieldMap.put(field, keyWord);
                        }

                        QueryResponse queryResponse = elkClientService.luceneQuery(index, null, fieldMap, 0, new Long(zSetCapacity + zSetCacheCapacity).intValue());
                        Hit[] hits = queryResponse.getHits().getHits();

                        for (Hit hit : hits) {
                            if (hit.get_source() != null) {
                                for (String field : fields) {
                                    redisClientService.zAdd(zSetKey, String.valueOf(hit.get_source().get(field)), hit.get_score().doubleValue());
                                }
                            } else {
                                for (String field : fields) {
                                    redisClientService.zAdd(zSetKey, String.valueOf(hit.getFields().get(field)[0]), hit.get_score().doubleValue());
                                }
                            }
                        }
                    }
                }
            } finally {
                rLock.unlock();
            }
        }
    }
}