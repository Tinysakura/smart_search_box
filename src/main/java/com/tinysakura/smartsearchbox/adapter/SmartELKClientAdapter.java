package com.tinysakura.smartsearchbox.adapter;

import com.tinysakura.bean.index.Index;
import com.tinysakura.core.query.QueryBody;
import com.tinysakura.core.query.base.FuzzyQuery;
import com.tinysakura.core.query.base.IdsQuery;
import com.tinysakura.core.query.base.PrefixQuery;
import com.tinysakura.core.query.base.TermQuery;
import com.tinysakura.net.client.RetrofitProxyServiceHolder;
import com.tinysakura.net.retrofit.service.DocumentService;
import com.tinysakura.net.retrofit.service.IndexService;
import com.tinysakura.net.retrofit.service.QueryService;
import com.tinysakura.smartsearchbox.service.ELKClientService;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用个人开源项目smart_elk_client{guthub@https://github.com/Tinysakura/smart_elk_client}作为elastic search java客户端
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
public class SmartELKClientAdapter implements ELKClientService {

    private IndexService indexService;

    private DocumentService documentService;

    private QueryService queryService;

    public SmartELKClientAdapter() {
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
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        TermQuery.Builder termQueryBuilder = new TermQuery.Builder();
        TermQuery termQuery = termQueryBuilder.field(field).value(value).boost(boost).build();
        QueryBody queryBody = queryBodyBuilder.query(termQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        return commonQuery(index, documentType, queryBody, clazz);
    }

    @Override
    public List<Object> prefixQuery(String index, String documentType, String field, String prefix, Double boost, Integer pageIndex, Integer pageSize, Class clazz) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        PrefixQuery.Builder prefixQueryBuilder = new PrefixQuery.Builder();
        PrefixQuery prefixQuery = prefixQueryBuilder.fields(field).prefix(prefix).boost(boost).build();
        QueryBody queryBody = queryBodyBuilder.query(prefixQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        return commonQuery(index, documentType, queryBody, clazz);
    }

    @Override
    public List<Object> fuzzyQuery(String index, String documentType, String field, String likeText, Double minSimilarity, Double boost, Integer pageIndex, Integer pageSize, Class clazz) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        FuzzyQuery.Builder fuzzyQueryBuilder = new FuzzyQuery.Builder();
        FuzzyQuery fuzzyQuery = fuzzyQueryBuilder.field(field).likeText(likeText).boost(boost).build();
        QueryBody queryBody = queryBodyBuilder.query(fuzzyQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

        return commonQuery(index, documentType, queryBody, clazz);
    }

    @Override
    public Object idsQuery(String index, String documentType, String id, Integer pageIndex, Integer pageSize, Class clazz) {
        QueryBody.Builder queryBodyBuilder = new QueryBody.Builder();
        IdsQuery.Builder idsQueryBuilder = new IdsQuery.Builder();
        IdsQuery idsQuery = idsQueryBuilder.documentType(documentType).documentIds(new String[]{id}).build();
        QueryBody queryBody = queryBodyBuilder.query(idsQuery.getQuery()).from((pageIndex - 1) * pageSize).size(pageSize).build();

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