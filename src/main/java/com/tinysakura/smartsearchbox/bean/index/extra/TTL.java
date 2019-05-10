package com.tinysakura.smartsearchbox.bean.index.extra;

import lombok.Data;

/**
 * _ttl指定了文档的生命周期，周期结束后文档会被自动删除
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/4
 */
@Data
public class TTL {
    /**
     * 是否启用_ttl字段
     */
    private boolean enabled;

    /**
     * 默认过期时间，如果需要30天后过期则 "default" : "30d"
     */
    private String defaults;
}