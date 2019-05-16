package com.tinysakura.smartsearchbox.service;


import com.tinysakura.bean.index.Index;
import com.tinysakura.bean.query.result.QueryResponse;
import com.tinysakura.smartsearchbox.common.entity.DocumentScore;

import java.util.List;
import java.util.Map;

/**
 * 项目需要用到的elastic search java客户端提供的服务接口
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */

public interface ElkClientService {
    /**
     * 索引创建相关接口
     */
    void createIndex(String indexName);

    /**
     * 根据索引映射配置创建索引
     * @param indexName
     * @param index 索引映射
     */
    void createIndex(String indexName, Index index);

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
     * @param pageIndex
     * @param pageSize
     * @param clazz
     */
    List<DocumentScore> termQuery(String index, String documentType, String field, Object value, Double boost, Integer pageIndex, Integer pageSize, Class clazz);

    List<DocumentScore> termQuery(String index, String documentType, String field, Object value, Double boost, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags);


    /**
     * 前缀查询
     * @param index
     * @param documentType
     * @param field
     * @param prefix
     * @param boost
     * @param pageIndex
     * @param pageSize
     * @param clazz
     */
    List<DocumentScore> prefixQuery(String index, String documentType, String field, String prefix, Double boost, Integer pageIndex, Integer pageSize, Class clazz);

    List<DocumentScore> prefixQuery(String index, String documentType, String field, String prefix, Double boost, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags);

    /**
     * 模糊查询
     * @param index
     * @param documentType
     * @param field
     * @param likeText
     * @param minSimilarity 最小相似度
     * @param boost
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return
     */
    List<DocumentScore> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost, Integer pageIndex, Integer pageSize, Class clazz);

    List<DocumentScore> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags);

    /**
     * multiMatch查询
     * @param index
     * @param documentType
     * @param fields
     * @param text
     * @param analyzer
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return
     */
    List<DocumentScore> multiMatchQuery(String index, String documentType, String[] fields, String text, String analyzer, Integer pageIndex, Integer pageSize, Class clazz);

    QueryResponse multiMatchQuery(String index, String documentType, String[] fields, String text, String analyzer, Integer pageIndex, Integer pageSize);

    List<DocumentScore> multiMatchQuery(String index, String documentType, String[] fields, String text, String analyzer, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags);

    List<DocumentScore> luceneQuery(String index, String documentType, Map<String, Object> fields, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags);

    /**
     * 文档标识符查询
     * @param index
     * @param documentType
     * @param id
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return
     */
    DocumentScore idsQuery(String index, String documentType, String id, Integer pageIndex, Integer pageSize, Class clazz);

    DocumentScore idsQuery(String index, String documentType, String id, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags);
}