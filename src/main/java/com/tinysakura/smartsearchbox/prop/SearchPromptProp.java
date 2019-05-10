package com.tinysakura.smartsearchbox.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 搜索推荐相关配置类
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Data
@Configuration
public class SearchPromptProp {
    @Value("smart_search_box.search.prompt.behavior.ratio")
    private Double behaviorRatio;

    @Value("smart_search_box.search.prompt.document.ratio")
    private Double documentRatio;

    @Value("smart_search_box.search.prompt.number")
    private Integer number;

    @Value("smart_search_box.search_prompt.zset.capacity")
    private Integer zsetCapacity;

    @Bean
    public SearchPromptProp searchPromptProp() {
        SearchPromptProp searchPromptProp = new SearchPromptProp();
        searchPromptProp.setBehaviorRatio(this.behaviorRatio);
        searchPromptProp.setDocumentRatio(this.documentRatio);
        searchPromptProp.setNumber(this.number);
        searchPromptProp.setZsetCapacity(this.zsetCapacity);

        return searchPromptProp;
    }
}