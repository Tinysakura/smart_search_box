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

    @Bean
    public IndexProp indexInitProp() {
        IndexProp indexInitProp = new IndexProp();
        indexInitProp.setIndexInitThreadPoolSize(this.indexInitThreadPoolSize);
        indexInitProp.setDocumentIndexThreadPoolSize(this.documentIndexThreadPoolSize);

        return indexInitProp;
    }
}