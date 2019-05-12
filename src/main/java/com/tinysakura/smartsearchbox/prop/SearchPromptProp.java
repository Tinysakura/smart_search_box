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
    @Value("smart_search_box.search_prompt.behavior.ratio")
    private Double behaviorRatio;

    @Value("smart_search_box.search_prompt.document.ratio")
    private Double documentRatio;

    @Value("smart_search_box.search_prompt.number")
    private Integer number;

    @Value("smart_search_box.search_prompt.zset.capacity")
    private Long zSetCapacity;

    @Value("smart_search_box.search_prompt.zset.cache_capacity")
    private Long zSetCacheCapacity;

    @Value("smart_search_box.search_prompt.document_zset.key")
    private String documentZSetKey;

    @Value("smart_search_box.search_prompt.behavior_zset.key")
    private String behaviorZSetKey;

    @Value("smart_search_box.search_prompt.highlight.pre_tags")
    private String preTags;

    @Value("smart_search_box.search_prompt.highlight.post_tags")
    private String postTags;

    @Bean
    public SearchPromptProp searchPromptProp() {
        SearchPromptProp searchPromptProp = new SearchPromptProp();
        searchPromptProp.setBehaviorRatio(this.behaviorRatio);
        searchPromptProp.setDocumentRatio(this.documentRatio);
        searchPromptProp.setNumber(this.number);
        searchPromptProp.setZSetCapacity(this.zSetCapacity);
        searchPromptProp.setZSetCacheCapacity(this.zSetCacheCapacity);
        searchPromptProp.setDocumentZSetKey(this.documentZSetKey);
        searchPromptProp.setBehaviorZSetKey(this.behaviorZSetKey);
        searchPromptProp.setPreTags(this.preTags);
        searchPromptProp.setPostTags(this.postTags);

        return searchPromptProp;
    }
}