package com.tinysakura.smartsearchbox.service;

import java.util.Map;
import java.util.Set;

/**
 * 项目需要用到的redis java客户端提供的服务接口
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */

public interface RedisClientService {

    /**
     * 基础操作相关接口
     */

    /**
     * 判断key是否已存在
     * @param key
     * @return
     */
    Boolean exists(String key);

    /**
     * 删除key对应的value
     * @param key
     */
    void del(String key);

    /**
     * sorted set操作相关接口
     */

    /**
     * 创建一个具有最大数量限制的zset
     * @param key
     * @param map
     */
    void zSet(String key, Map<String, Double> map);

    /**
     * 向zset中添加新元素
     * @param key
     * @param value
     * @param score
     */
    void zAdd(String key, String value, Double score);

    /**
     * 按score升序返回集合指定区间的元素
     * @param key
     * @param from
     * @param to
     */
    Set<String> zRange(String key, Integer from, Integer to);

    /**
     * 按score降序返回指定区间的元素
     * @param key
     * @param from
     * @param to
     * @return
     */
    Set<String> zrevRange(String key, Integer from, Integer to);

    /**
     * 返回集合中指定分数区间的元素
     * @param key
     * @param from
     * @param to
     * @return
     */
    Set<String> zRangeByScore(String key, Double from, Double to);

    /**
     * 删除结合中的指定元素
     * @param key
     * @param members
     */
    void zRem(String key, String[] members);

    /**
     * 删除指定区间的所有元素
     * @param key
     * @param from
     * @param to
     */
    void zRemByRange(String key, Integer from, Integer to);

    /**
     * 删除指定分数区间的所有元素
     * @param key
     * @param from
     * @param to
     */
    void zRemByScoreRange(String key, Double from, Double to);

    /**
     * 计算指定集合的并集
     * @return
     */
    Set<String> zUnion(String... keys);

    /**
     * 把指定集合中指定value的分数加上指定的值
     * @param key
     * @param score
     * @param value
     */
    void zincrby(String key, Double score, String value);

    /**
     * 普通set操作相关接口
     */

    /**
     * 向普通set中添加一个元素
     * @param key
     * @param value
     */
    void sAdd(String key, String... value);

    /**
     * 获取普通set中的所有元素
     * @param key
     * @return
     */
    Set<String> sMembers(String key);
}