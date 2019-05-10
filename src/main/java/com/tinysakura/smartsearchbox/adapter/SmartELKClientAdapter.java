package com.tinysakura.smartsearchbox.adapter;

import com.tinysakura.smartsearchbox.bean.index.Index;
import com.tinysakura.smartsearchbox.service.ELKClientService;

import java.util.List;

/**
 * 使用个人开源项目smart_elk_client{guthub@https://github.com/Tinysakura/smart_elk_client}作为elastic search java客户端
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
public class SmartELKClientAdapter implements ELKClientService {
    @Override
    public void createIndex(String indexName) {

    }

    @Override
    public void createIndex(String indexName, String documentType) {

    }

    @Override
    public void createIndex(String indexName, String documentType, Index index) {

    }

    @Override
    public void addDocument(String indexName, String documentType, String documentId, Object document) {

    }

    @Override
    public void addDocument(String indexName, String documentType, Object document) {

    }

    @Override
    public List<Object> termQuery(String index, String documentType, String field, Object value, Double boost) {
        return null;
    }

    @Override
    public List<Object> prefixQuery(String index, String documentType, String field, String prefix, Double boost) {
        return null;
    }

    @Override
    public List<Object> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost) {
        return null;
    }

    @Override
    public Object idsQuery(String index, String documentType, String id) {
        return null;
    }
}