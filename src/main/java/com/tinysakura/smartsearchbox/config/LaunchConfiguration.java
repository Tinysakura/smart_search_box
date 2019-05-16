package com.tinysakura.smartsearchbox.config;

import com.tinysakura.smartsearchbox.adapter.AnsjAnalyzerAdapter;
import com.tinysakura.smartsearchbox.adapter.IkAnalyzerAdapter;
import com.tinysakura.smartsearchbox.adapter.JedisClientAdapter;
import com.tinysakura.smartsearchbox.adapter.SmartElkClientAdapter;
import com.tinysakura.smartsearchbox.config.prop.EndPointProp;
import com.tinysakura.smartsearchbox.config.prop.IndexProp;
import com.tinysakura.smartsearchbox.config.prop.SearchPromptProp;
import com.tinysakura.smartsearchbox.core.Launch;
import com.tinysakura.smartsearchbox.service.AnalyzerService;
import com.tinysakura.smartsearchbox.service.ElkClientService;
import com.tinysakura.smartsearchbox.service.RedisClientService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * 使用默认的配置启动并注入Launch，如果不想使用框架提供的默认实现可以配置扫描该包路径时忽略该config类即可
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */
@Configuration
@Import({RedisConfiguration.class, RedissionConfiguration.class})
public class LaunchConfiguration {
    /**
     * data
     */
    @Value("${smart_search_box.search_prompt.sensitive_word}")
    private String sensitiveWord;

    @Value("${smart_search_box.index_init.thread_pool.size}")
    private String indexInitThreadPoolSize;

    @Value("${smart_search_box.document_index.thread_pool.size}")
    private String documentIndexThreadPoolSize;

    @Value("${smart_search_box.index_query.highlight.pre_tags}")
    private String highlightPreTags;

    @Value("${smart_search_box.index_query.highlight.post_tags}")
    private String highlightPostTags;

    @Value("${smart_search_box.index_query.default_analyzer}")
    private String defaultAnalyzer;

    @Value("${smart_search_box.document_index.async}")
    private String async;

    @Value("${smart_search_box.annotation.document.scan.package}")
    private String documentAnnotationScanPath;

    @Value("${smart_search_box.annotation.index.scan.package}")
    private String indexAnnotationScanPath;

    @Value("${smart_search_box.index_query.document_classes}")
    private String documentClasses;

    @Value("${smart_search_box.search_prompt.behavior.ratio}")
    private String behaviorRatio;

    @Value("${smart_search_box.search_prompt.document.ratio}")
    private String documentRatio;

    @Value("${smart_search_box.search_prompt.zset.capacity}")
    private String zSetCapacity;

    @Value("${smart_search_box.search_prompt.zset.cache_capacity}")
    private String zSetCacheCapacity;

    @Value("${smart_search_box.search_prompt.behavior_zset.clean_up_interval}")
    private String behaviorZSetCleanUpInterval;

    @Value("${smart_search_box.search_prompt.highlight.pre_tags}")
    private String preTags;

    @Value("${smart_search_box.search_prompt.highlight.post_tags}")
    private String postTags;

    @Resource(name = "jedisPool")
    private JedisPool jedisPool;

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public EndPointProp endPointProp() {
        EndPointProp endPointProp = new EndPointProp();
        endPointProp.setSensitiveWord(this.sensitiveWord);
        String[] splits = sensitiveWord.split(",");
        endPointProp.setSensitiveWords(splits);

        return endPointProp;
    }

    @Bean
    public IndexProp indexInitProp() {
        IndexProp indexProp = new IndexProp();
        indexProp.setIndexInitThreadPoolSize(new Integer(this.indexInitThreadPoolSize));
        indexProp.setDocumentIndexThreadPoolSize(new Integer(this.documentIndexThreadPoolSize));
        indexProp.setDefaultAnalyzer(this.defaultAnalyzer);
        indexProp.setHighlightPreTags(this.highlightPreTags);
        indexProp.setHighlightPostTags(this.highlightPostTags);
        indexProp.setAsync(new Boolean(this.async));
        indexProp.setDocumentAnnotationScanPath(this.documentAnnotationScanPath);
        indexProp.setIndexAnnotationScanPath(this.indexAnnotationScanPath);
        indexProp.setClasses(this.documentClasses.split(","));

        return indexProp;
    }

    @Bean
    public SearchPromptProp searchPromptProp() {
        SearchPromptProp searchPromptProp = new SearchPromptProp();
        searchPromptProp.setBehaviorRatio(new Double(this.behaviorRatio));
        searchPromptProp.setDocumentRatio(new Double(this.documentRatio));
        searchPromptProp.setZSetCapacity(new Long(this.zSetCapacity));
        searchPromptProp.setZSetCacheCapacity(new Long(this.zSetCacheCapacity));
        searchPromptProp.setBehaviorZSetCleanUpInterval(new Long(this.behaviorZSetCleanUpInterval));
        searchPromptProp.setPreTags(this.preTags);
        searchPromptProp.setPostTags(this.postTags);

        return searchPromptProp;
    }

    @Bean
    public Launch launch() {
        Launch launch = new Launch(endPointProp(), indexInitProp(), searchPromptProp(), ansjAnalyzerAdapter(), smartElkClientAdapter(), jedisClientAdapter(), redissonClient);

        return launch;
    }

//    @Bean
//    public AnalyzerService ikAnalyzerAdapter() {
//        IkAnalyzerAdapter ikAnalyzerAdapter = new IkAnalyzerAdapter(false);
//
//        return ikAnalyzerAdapter;
//    }

    @Bean
    public AnalyzerService  ansjAnalyzerAdapter() {
        AnsjAnalyzerAdapter ansjAnalyzerAdapter = new AnsjAnalyzerAdapter();

        return ansjAnalyzerAdapter;
    }

    @Bean
    public RedisClientService jedisClientAdapter() {
        JedisClientAdapter jedisClientAdapter = new JedisClientAdapter(jedisPool, searchPromptProp().getZSetCapacity(), searchPromptProp().getZSetCacheCapacity());

        return jedisClientAdapter;
    }

    @Bean
    public ElkClientService smartElkClientAdapter() {
        ElkClientService smartElkClientAdapter = new SmartElkClientAdapter();

        return smartElkClientAdapter;
    }

}