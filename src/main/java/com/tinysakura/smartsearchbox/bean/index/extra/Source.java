package com.tinysakura.smartsearchbox.bean.index.extra;

import lombok.Data;

/**
 * _source字段在生成索引过程中存储原始json文档
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/4
 */
@Data
public class Source {
    /**
     * 是否启用_source字段
     */
    private boolean enabled;
}