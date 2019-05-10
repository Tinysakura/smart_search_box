package com.tinysakura.smartsearchbox.bean.index.extra;

import lombok.Data;

/**
 * 索引结构附加内部信息bean
 *
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/4
 */
@Data
public class Id {
    /**
     * 指定使用索引文档中指定字段的值作为文档标识符
     */
    private String path;
}