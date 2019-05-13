package com.tinysakura.smartsearchbox.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 索引相关配置
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Configuration
@Data
public class IndexProp {

    @Value("smart_search_box.index_init.thread_pool.size")
    private Integer indexInitThreadPoolSize;

    @Value("smart_search_box.document_index.thread_pool.size")
    private Integer documentIndexThreadPoolSize;

    @Value("smart_search_box.index_query.highlight.pre_tags")
    private String highlightPreTags;

    @Value("smart_search_box.index_query.highlight.post_tags")
    private String highlightPostTags;

    @Value("smart_search_box.index_query.default_analyzer")
    private String defaultAnalyzer;

    @Value("smart_search_box.document_index.async")
    private boolean async;

    @Value("smart_search_box.annotation.document.scan.package")
    private String documentAnnotationScanPath;

    @Value("smart_search_box.annotation.index.scan.package")
    private String indexAnnotationScanPath;

    @Bean
    public IndexProp indexInitProp() {
        IndexProp indexProp = new IndexProp();
        indexProp.setIndexInitThreadPoolSize(this.indexInitThreadPoolSize);
        indexProp.setDocumentIndexThreadPoolSize(this.documentIndexThreadPoolSize);
        indexProp.setDefaultAnalyzer(this.defaultAnalyzer);
        indexProp.setHighlightPreTags(this.highlightPreTags);
        indexProp.setHighlightPostTags(this.highlightPostTags);
        indexProp.setAsync(this.async);
        indexProp.setDocumentAnnotationScanPath(this.documentAnnotationScanPath);
        indexProp.setIndexAnnotationScanPath(this.indexAnnotationScanPath);

        return indexProp;
    }
}