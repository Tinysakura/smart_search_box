package com.tinysakura.smartsearchbox.bean.index.extra;

import lombok.Data;

/**
 * _timestamp字段允许文档在索引时被存储
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/4
 */
@Data
public class Timestamp {
    /**
     * 是否启用_timestamp字段
     */
    private boolean enabled;
}