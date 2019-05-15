package com.tinysakura.smartsearchbox.config.prop;

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

    private Integer indexInitThreadPoolSize;

    private Integer documentIndexThreadPoolSize;

    private String highlightPreTags;

    private String highlightPostTags;

    private String defaultAnalyzer;

    private boolean async;

    private String documentAnnotationScanPath;

    private String indexAnnotationScanPath;

    private String documentClasses;

    private String[] classes;
}