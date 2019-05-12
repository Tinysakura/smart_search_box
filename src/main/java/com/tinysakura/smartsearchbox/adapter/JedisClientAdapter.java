package com.tinysakura.smartsearchbox.adapter;

import com.tinysakura.smartsearchbox.service.RedisClientService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 使用jedis作为redis java客户端
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */
public class JedisClientAdapter implements RedisClientService {
    private JedisPool jedisPool;

    private Long zSetCapacity;

    private Long zSetCacheCapacity;

    public JedisClientAdapter(JedisPool jedisPool, Long zSetCapacity, Long zSetCacheCapacity) {
        this.jedisPool = jedisPool;
        this.zSetCapacity = zSetCapacity;
        this.zSetCacheCapacity = zSetCacheCapacity;
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public void zSet(String key, Map<String, Double> map) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, map);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public void zAdd(String key, String value, Double score) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            /**
             * 为了提高性能，超出单个zSet限制容量数据的清理工作交给定时任务来做
             */
            jedis.zadd(key, score, value);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public Set<String> zRange(String key, Integer from, Integer to) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            Set<String> set = jedis.zrange(key, from, to);

            return set;
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public Set<String> zRangeByScore(String key, Double from, Double to) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            Set<String> set = jedis.zrangeByScore(key, from, to);

            return set;
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public void zRem(String key, String[] members) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            jedis.zrem(key, members);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public void zRemByRange(String key, Integer from, Integer to) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            jedis.zremrangeByRank(key, from, to);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public void zRemByScoreRange(String key, Double from, Double to) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            jedis.zremrangeByScore(key, from, to);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public Set<String> zUnion(String... keys) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();

            Long capacity = this.zSetCacheCapacity + this.zSetCapacity;

            Set<String> unionSet = new HashSet<>();

            for (String key : keys) {
                unionSet.addAll(jedis.zrange(key, 0, capacity));
            }

            return unionSet;
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    @Override
    public void sAdd(String key, String... value) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();

            jedis.sadd(key, value);
        } finally {
            jedis.close();
        }
    }
}