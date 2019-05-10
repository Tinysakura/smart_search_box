package com.tinysakura.smartsearchbox.bean.index;

import com.tinysakura.smartsearchbox.bean.document.DocumentType;
import lombok.Data;

import java.util.Map;

/**
 * 索引操作相关请求体
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/1
 */
@Data
public class Index {
    private Map<String, DocumentType> mappings;
    private Setting settings;
}