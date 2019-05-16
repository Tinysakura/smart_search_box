package com.tinysakura.smartsearchbox.common.dto;

import lombok.Data;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/16
 */
@Data
public class DocumentSearchDto {

    /**
     * {@value ${smart_search_box.index_query.document_classes}}
     */
    private Integer documentType;

    private String keyword;

    private Integer index;

    private Integer size;

    private String[] fields;
}