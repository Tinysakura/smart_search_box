package com.tinysakura.smartsearchbox.common.entity;

import lombok.Data;

/**
 * 包含得分信息的文档
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */
@Data
public class DocumentScore {
    /**
     * 文档
     */
    private Object document;

    /**
     * 文档得分
     */
    private Float score;
}