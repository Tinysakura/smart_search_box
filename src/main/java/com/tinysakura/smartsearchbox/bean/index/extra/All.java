package com.tinysakura.smartsearchbox.bean.index.extra;

import lombok.Data;

/**
 * 索引结构附加内部信息bean
 * _all字段中存储其它所有字段的数据以便于搜索
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/4
 */
@Data
public class All {
    /**
     * 是否启用_all字段
     */
    private boolean enabled;
}