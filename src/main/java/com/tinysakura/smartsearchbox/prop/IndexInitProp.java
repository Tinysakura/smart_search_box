package com.tinysakura.smartsearchbox.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 索引初始化相关配置
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Configuration
@Data
public class IndexInitProp {

    @Value("smart_search_box.index_init.thread_pool.size")
    private Integer threadPoolSize;

    @Bean
    public IndexInitProp indexInitProp() {
        IndexInitProp indexInitProp = new IndexInitProp();
        indexInitProp.setThreadPoolSize(this.threadPoolSize);

        return indexInitProp;
    }
}