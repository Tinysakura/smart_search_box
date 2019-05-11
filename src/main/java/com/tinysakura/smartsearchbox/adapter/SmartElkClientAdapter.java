package com.tinysakura.smartsearchbox.adapter;

import com.tinysakura.bean.index.Index;
import com.tinysakura.core.query.QueryBody;
import com.tinysakura.core.query.base.FuzzyQuery;
import com.tinysakura.core.query.base.IdsQuery;
import com.tinysakura.core.query.base.PrefixQuery;
import com.tinysakura.core.query.base.TermQuery;
import com.tinysakura.core.query.highlight.HighLightQuery;
import com.tinysakura.net.client.RetrofitProxyServiceHolder;
import com.tinysakura.net.retrofit.service.DocumentService;
import com.tinysakura.net.retrofit.service.IndexService;
import com.tinysakura.net.retrofit.service.QueryService;
import com.tinysakura.smartsearchbox.service.ElkClientService;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用个人开源项目smart_elk_client{guthub@https://github.com/Tinysakura/smart_elk_client}作为elastic search java客户端
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
public class SmartElkClientAdapter implements ElkClientService {

    private IndexService indexService;

    private DocumentService documentService;

    private QueryService queryService;

    public SmartElkClientAdapter() {
        initService();
    }

    private void initService() {
        this.indexService = RetrofitProxyServiceHolder.getInstance().getIndexServiceProxy();
        this.documentService = RetrofitProxyServiceHolder.getInstance().getDocumentServiceProxy();
        this.queryService = RetrofitProxyServiceHolder.getInstance().getQueryServiceProxy();
    }


    @Override
    public void createIndex(String indexName) {
        indexService.createIndex(indexName);
    }

    @Override
    public void createIndex(String indexName, Index index) {
        indexService.createIndex(indexName, index);
    }

    @Override
    public void addDocument(String indexName, String documentType, String documentId, Object document) {
        documentService.postDocument(indexName, documentType, documentId, document);
    }

    @Override
    public void addDocument(String indexName, String documentType, Object document) {
        documentService.postDocument(indexName, documentType, document);
    }

    @Override
    public List<Object> termQuery(String index, String documentType, String field, Object value, Double boost, Integer pageIndex, Integer pageSize, Class clazz) {
        return termQuery(index, documentType, field, value, boost, pageIndex, pageSize, clazz, null, null);
    }

    @Override
    public List<Object> termQuery(String index, String documentType, String field, Object value, Double boost, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        TermQuery.Builder termQueryBuilder = new TermQuery.Builder();
        TermQuery termQuery = termQueryBuilder.field(field).value(value).boost(boost).build();
        queryBodyBuilder.query(termQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        if (preTags != null && postTags != null) {
            HighLightQuery.Builder highlightQueryBuilder = new HighLightQuery.Builder();
            HighLightQuery highLightQuery = highlightQueryBuilder.field(field).preTags(new String[]{preTags}).postTags(new String[]{postTags}).build();
            queryBodyBuilder.highlight(highLightQuery.getHighLightEntry());
        }

        QueryBody queryBody = queryBodyBuilder.build();
        return commonQuery(index, documentType, queryBody, clazz);
    }

    @Override
    public List<Object> prefixQuery(String index, String documentType, String field, String prefix, Double boost, Integer pageIndex, Integer pageSize, Class clazz) {
        return prefixQuery(index, documentType, field, prefix, boost, pageIndex, pageSize, clazz, null, null);
    }

    @Override
    public List<Object> prefixQuery(String index, String documentType, String field, String prefix, Double boost, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        PrefixQuery.Builder prefixQueryBuilder = new PrefixQuery.Builder();
        PrefixQuery prefixQuery = prefixQueryBuilder.fields(field).prefix(prefix).boost(boost).build();
        queryBodyBuilder.query(prefixQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        if (preTags != null && postTags != null) {
            HighLightQuery.Builder highlightQueryBuilder = new HighLightQuery.Builder();
            HighLightQuery highLightQuery = highlightQueryBuilder.field(field).preTags(new String[]{preTags}).postTags(new String[]{postTags}).build();
            queryBodyBuilder.highlight(highLightQuery.getHighLightEntry());
        }
        QueryBody queryBody = queryBodyBuilder.build();

        return commonQuery(index, documentType, queryBody, clazz);
    }

    @Override
    public List<Object> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost, Integer pageIndex, Integer pageSize, Class clazz) {
        return fuzzyQuery(index, documentType, field, likeText, minSimilarity, boost, pageIndex, pageSize, clazz, null, null);
    }

    @Override
    public List<Object> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        FuzzyQuery.Builder fuzzyQueryBuilder = new FuzzyQuery.Builder();
        FuzzyQuery fuzzyQuery = fuzzyQueryBuilder.field(field).likeText(likeText).boost(boost).build();
        queryBodyBuilder.query(fuzzyQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        if (preTags != null && postTags != null) {
            HighLightQuery.Builder highlightQueryBuilder = new HighLightQuery.Builder();
            HighLightQuery highLightQuery = highlightQueryBuilder.field(field).preTags(new String[]{preTags}).postTags(new String[]{postTags}).build();
            queryBodyBuilder.highlight(highLightQuery.getHighLightEntry());
        }

        QueryBody queryBody = queryBodyBuilder.build();
        return commonQuery(index, documentType, queryBody, clazz);
    }

    @Override
    public Object idsQuery(String index, String documentType, String id, Integer pageIndex, Integer pageSize, Class clazz) {
        return idsQuery(index, documentType, id, pageIndex, pageSize, clazz, null, null);
    }

    @Override
    public Object idsQuery(String index, String documentType, String id, Integer pageIndex, Integer pageSize, Class clazz, String preTags, String postTags) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        IdsQuery.Builder idsQueryBuilder = new IdsQuery.Builder();
        IdsQuery idsQuery = idsQueryBuilder.documentType(documentType).documentIds(new String[]{id}).build();
        queryBodyBuilder.query(idsQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        if (preTags != null && postTags != null) {
            HighLightQuery.Builder highlightQueryBuilder = new HighLightQuery.Builder();
            HighLightQuery highLightQuery = highlightQueryBuilder.globalPreTags(new String[]{preTags}).gloabalPostTags(new String[]{postTags}).build();
            queryBodyBuilder.highlight(highLightQuery.getHighLightEntry());
        }

        QueryBody queryBody = queryBodyBuilder.build();
        return commonQuery(index, documentType, queryBody, clazz);
    }

    private List<Object> commonQuery(String index, String documentType, QueryBody queryBody, Class clazz) {
        List<Object>[] queryResults = new List[1];

        /**
         * 当出现异常时返回一个空的列表
         */
        queryService.search(index, documentType, queryBody.getQueryBody(), clazz).doOnError(throwable -> queryResults[0] = new ArrayList<>()).subscribe(queryResponse -> queryResults[0] = queryResponse.getResults());

        return queryResults[0];
    }
}