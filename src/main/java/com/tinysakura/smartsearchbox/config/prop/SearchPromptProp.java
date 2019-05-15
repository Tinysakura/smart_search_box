package com.tinysakura.smartsearchbox.config.prop;

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
    private Double behaviorRatio;

    private Double documentRatio;

    private Long zSetCapacity;

    private Long zSetCacheCapacity;

    private Long behaviorZSetCleanUpInterval;

    private String preTags;

    private String postTags;
}