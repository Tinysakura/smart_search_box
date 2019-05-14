package com.tinysakura.smartsearchbox.common;

import lombok.Data;

/**
 * 通用的响应结果
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/14
 */
@Data
public class ResponseView<T> {
    /**
     * {@link com.tinysakura.smartsearchbox.constant.enums.ResponseCodeEnum}
     */
    Integer code;

    String message;

    T result;
}