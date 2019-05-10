package com.tinysakura.smartsearchbox.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * 搜索推荐相关配置类
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Data
public class SearchPromptProp {
    @Value("smart_search_box.search.prompt.behavior.ratio")
    private Double behaviorRatio;

    @Value("smart_search_box.search.prompt.document.ratio")
    private Double documentRatio;

    @Value("smart_search_box.search.prompt.number")
    private Integer number;

    @Value("smart_search_box.search_prompt.zset.capacity")
    private Integer zsetCapacity;
}