package com.tinysakura.smartsearchbox.bean.index.extra;

import lombok.Data;

/**
 * _size字段存储_source字段的原始大小
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/4
 */
@Data
public class Size {
    /**
     * 是否启用_size字段
     */
    private boolean enabled;

    /**
     * _size字段是否被存储{@link com.tinysakura.smartsearchbox.constant.DocumentPropertiesConstant.Store}
     */
    private String store;
}