package com.tinysakura.smartsearchbox.common;

import lombok.Data;

import java.util.List;

/**
 * 分页的通用响应结果
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Data
public class PaginationResponseView<T> {
    private Integer code;

    private String message;

    private List<T> results;

    private Pagination pagination;
}