package com.tinysakura.smartsearchbox.common;

import lombok.Data;

/**
 * 分页查询配置类
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Data
public class Pagination {
    /**
     * 指定单页文档数量
     */
    private Integer size;

    /**
     * 指定初始页
     */
    private Integer index;
}