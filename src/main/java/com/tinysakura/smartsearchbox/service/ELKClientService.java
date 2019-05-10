package com.tinysakura.smartsearchbox.service;

import com.tinysakura.smartsearchbox.bean.index.Index;

import java.util.List;

/**
 * 项目需要用到的elastic search java客户端提供的服务接口
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */

public interface ELKClientService {
    /**
     * 索引创建相关接口
     */
    void createIndex(String indexName);

    void createIndex(String indexName, String documentType);

    /**
     * 根据索引映射配置创建索引
     * @param indexName
     * @param documentType
     * @param index 索引映射
     */
    void createIndex(String indexName, String documentType, Index index);

    /**
     * 索引文档相关接口
     */
    void addDocument(String indexName, String documentType, String documentId, Object document);

    void addDocument(String indexName, String documentType, Object document);

    /**
     * 文档查询相关接口
     */

    /**
     * term查询
     * @param index
     * @param documentType
     * @param field
     * @param value
     * @param boost
     */
    List<Object> termQuery(String index, String documentType, String field, Object value, Double boost);

    /**
     * 前缀查询
     * @param index
     * @param documentType
     * @param field
     * @param prefix
     * @param boost
     */
    List<Object> prefixQuery(String index, String documentType, String field, String prefix, Double boost);

    /**
     * 模糊查询
     * @param index
     * @param documentType
     * @param field
     * @param likeText
     * @param minSimilarity 最小相似度
     * @param boost
     * @return
     */
    List<Object> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost);

    /**
     * 文档标识符查询
     * @param index
     * @param documentType
     * @param id
     * @return
     */
    Object idsQuery(String index, String documentType, String id);
}