package com.tinysakura.smartsearchbox.bean.index;

import lombok.Data;

import java.util.Map;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/1
 */
@Data
public class Setting {
    /**
     * 分片数量
     */
    private Integer number_of_shards;

    /**
     * 副本数量
     */
    private Integer number_of_replicas;
}