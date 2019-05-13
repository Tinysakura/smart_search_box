package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.RedisClientService;

import java.util.Set;

/**
 * 负责维护用户行为相关的zset的job
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */

public class UserBehaviorZSetCleanUpJob implements Runnable {

    private RedisClientService redisClientService;

    private Long zSetCapacity;

    private Long zSetCacheCapacity;

    public UserBehaviorZSetCleanUpJob(RedisClientService redisClientService, Long zSetCapacity, Long zSetCacheCapacity) {
        this.redisClientService = redisClientService;
        this.zSetCapacity = zSetCapacity;
        this.zSetCacheCapacity = zSetCacheCapacity;
    }

    @Override
    public void run() {
        /**
         * 从三级存储结构的最上层取出所有用户行为相关的set keys
         * keyFormat : {索引名}_{用户行为类型set后缀}
         */
        Set<String> behaviorSetKeys = redisClientService.sMembers(Launch.BEHAVIOR_SETS_KEYS_SET_KEY);

        for (String behaviorSetKey : behaviorSetKeys) {
            Set<String> behaviorZSetKeys = redisClientService.sMembers(behaviorSetKey);

            /**
             * 用户行为对应的zset清理比较简单，清理溢出的数据即可
             */
            for (String zSetKey : behaviorZSetKeys) {
                redisClientService.zRemByRange(zSetKey, new Long(zSetCapacity + zSetCacheCapacity).intValue(), -1);
            }
        }
    }
}