### recommend
smart_search_box是java领域的一站式搜索引擎解决方案，用户只需要进行简单的配置就可以在项目中引入完整的全文搜索能力，包括数据入库自动索引，搜索词联想，热词提示，高亮等功能。同时提供给用户高度的自定义能力，用户可以替换框架的部分实现，包括替换elastic search客户端，替换分词器，替换redis客户端等，只需要实现对应的接口替换默认注入的相应组件即可。默认使用[smart_elk_client](https://github.com/Tinysakura/smart_elk_client)作为elk客户端，使用ansj作为中文分词器，使用jedis作为redis客户端。

### qucick start
**1 clone项目**
由于smart_search_box对于个人的另外一个开源项目smart_elk_client有依赖，所以需要clone两个项目。
```shell
git clone git@github.com:Tinysakura/smart_elk_client.git
git clone git@github.com:Tinysakura/smart_search_box.git
```
**2 maven编译**
注意两个项目都要编译一下
```shell
mvn clean install -Dmaven.test.skip=true
```
**3 在项目pom中引入**
```xml
<dependency>
   <groupId>com.tinysakura</groupId>
   <artifactId>smart-search-box</artifactId>
   <version>0.0.1-RELEASE</version>
</dependency>
```

### usage
**1 配置项**
```shell
# 在指定包下扫描@Document注解与@Field注解<必选>
smart_search_box.annotation.document.scan.package=com.tinysakura.smartsearchbox.entity

# 在指定包下扫描@Index注解<必选>
smart_search_box.annotation.index.scan.package=com.tinysakura.smartsearchbox.dao

# 索引高亮查询前置标签<可选>
smart_search_box.index_query.highlight.pre_tags=<b>

# 索引高亮查询后置标签<可选>
smart_search_box.index_query.highlight.post_tags=</b>

# 默认使用的分析器<可选>
smart_search_box.index_query.default_analyzer=ik_smart

# 被索引的文档对应的实体类，不同类之间使用,分隔<必选>
smart_search_box.index_query.document_classes=com.tinysakura.smartsearchbox.entity.Book

# 是否异步索引文档<必选>
smart_search_box.document_index.async=false

# 搜索提示结果中对应用户行为的比例<必选>
smart_search_box.search_prompt.behavior.ratio=0.2

# 搜索提示结果中对应文档的比例<必选>
smart_search_box.search_prompt.document.ratio=0.8

# 指定用于存储搜索提示的zset容量<必选>
smart_search_box.search_prompt.zset.capacity=10

# 指定用于存储搜索提示的zset缓冲区域容量（即zset的总容量为capacity + cache_capacity）<必选>
smart_search_box.search_prompt.zset.cache_capacity=20

# 搜索提示敏感词<todo>
smart_search_box.search_prompt.sensitive_word=violence,pornographic

# 指定搜索提示高亮部分前置标签<可选>
smart_search_box.search_prompt.highlight.pre_tags=<b>

# 指定搜索提示高亮部分后置标签<可选>
smart_search_box.search_prompt.highlight.post_tags=</b>

# 清理用户行为对应的zset的间隔（单位毫秒）<必选>
smart_search_box.search_prompt.behavior_zset.clean_up_interval=1800000

# 初始化索引使用的线程池大小<必选>
smart_search_box.index_init.thread_pool.size=3

# 索引文档使用的线程池大小<必选>
smart_search_box.document_index.thread_pool.size=3

# 默认使用jedis作为redis java客户端
jedis.pool.host=127.0.0.1
jedis.pool.port=6379
jedis.pool.config.maxTotal=100 
jedis.pool.config.maxIdle=10
jedis.pool.config.maxWaitMill=100000

# elk节点
elk.node.ip=http://127.0.0.1:9200
```
如果要使用框架提供的默认的组件快速开始，请务必按照如上的配置项进行配置。如果需要使用自定义的redis和elk客户端请根据实际情况进行redis和elk相关配置，只需要保证对应的接口实现可以正确完整的提供相应服务即可。

**2 配置spring扫描框架的配置包**
```java
@SpringBootApplication(scanBasePackages = {"com.tinysakura.smartsearchbox.config")
```

**3 使用注解**
框架主要提供三个注解，@Document, @Field, @Index，下面分别介绍这三个注解

**@Document**
该注解用来配置索引基本属性与搜索提示相关配置，请在数据库表对应的实体类上使用该注解，注解有如下选项
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {
    /**
     * 指定索引名
     * @return
     */
    public String indexName();

    /**
     * 指定文档类型
     * @return
     */
    public String documentType() default "";

    /**
     * 指定搜索提示字段
     * @return
     */
    public String[] searchPromptFields() default "";

    /**
     * 指定分片数量
     * @return
     */
    public int shardsNumber() default 4;

    /**
     * 指定索引副本数量
     * @return
     */
    public int replicasNumber() default 1;

    /**
     * 是否开启动态类型猜测
     * @return
     */
    public boolean dynamic() default true;

    /**
     * 是否使用_all字段
     * @return
     */
    public boolean extraAll() default true;

    /**
     * 是否启用_size字段
     * @return
     */
    public boolean extraSize() default false;

    /**
     * 是否启用_timestamp字段
     * @return
     */
    public boolean extraTimestamp() default false;

    /**
     * 是否启用_source字段
     * @return
     */
    public boolean extraSource() default false;

    /**
     * 设置文档过期时间
     * @return
     */
    public String ttl() default "";
}
```

**@Field**
该注解用于索引映射配置，请在实体类中需要被存储到索引中的字段上使用该注解，注解有如下选项
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    /**
     * 是否将原始值写入索引
     * @return
     */
    public boolean storeOriginal() default true;

    /**
     * 指定字段在索引中的名称，默认使用原始属性名称
     * @return
     */
    public String fieldName() default "";

    public String type() default "text";

    /**
     * 指定字段权值
     * @return
     */
    public double boost() default 1;

    /**
     * 指定在该字段上使用的分词器
     * @return
     */
    public String analyzer() default "";

    /**
     * 指定索引时使用的日期格式
     * @return
     */
    public String dateFormat() default "";
}
```

**@Index**
该字段用于配置索引文档与搜索提示相关的行为，请在dao层对应的save方法上使用该注解（save方法必须有对应实体的返回值，绝大部分orm框架都是这么做的）。注解有如下选项。
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * 指定在哪个索引上索引文档
     * @return
     */
    public String index();

    /**
     * 指定文档类型
     * @return
     */
    public String documentType();

    /**
     * 指定使用文档哪个字段上的值作为文档标识符
     * @return
     */
    public String id() default "";

    /**
     * 指定文档索引表达式，只有符合表达式要求的文档最终才会被索引
     * @return
     */
    public String indexExpression() default "";

    /**
     * 指定搜索提示字段
     * @return
     */
    public String[] searchPromptFields() default "";
}
```

**4 使用框架提供的默认端点**
当按照步骤进行上述配置后，每次dao层的save操作都会按照配置进行文档的索引与搜索提示基础数据结构的划分，既然已经有了数据准备，接下来就是使用这些数据的功能，框架提供了默认的端点，用户无需自己再进行开发。

**GET /searchBox/{indexName}/searchPrompt**
搜索提示端点，根据用户在搜索框中输入的部分内容进行联想，接收以下参数

|参数名|类型|含义|  
|---|---|---|
|indexName|string|在哪个索引上进行联想|
|keyword|string|关键词|
|index|int|返回结果分页页数|
|size|int|返回结果分页大小|

```shell
curl http://localhost:8080/searchBox/mediiiia/searchPrompt?index=1&size=5&keyword=白

{
    "code": 0,
    "message": "正常响应",
    "results": [
        "<b>白</b>夜行"
    ],
    "pagination": {
        "size": 5,
        "index": 1
    }
}
```

**WS /search_prompt/prompt**
websocket订阅的端点，使用websocket获得搜索提示，对应的spring mvc端点如下
```java
@MessageMapping("/search_box/search_prompt")
@SendTo("/search_prompt/prompt")
```
|参数名|类型|含义|  
|---|---|---|
|indexName|string|在哪个索引上进行联想|
|keyword|string|关键词|
|index|int|返回结果分页页数|
|size|int|返回结果分页大小|


**POST /search_box/{indexName}/search_document**
文档查询端点，根据搜索内容返回指定索引的文档，接收如下结构的body
```java
@Data
public class DocumentSearchDto {

    /**
     * {@value ${smart_search_box.index_query.document_classes}}
     */
    private Integer documentType;

    // 搜索关键词
    private String keyword;

    // 分页页数
    private Integer index;

    // 分页大小
    private Integer size;
    
    //在文档哪些字段上搜索
    private String[] fields;
}
```
这里需要着重说一下documentType这个参数，这个参数不是指索引中的文档类型，而是对应配置中的smart_search_box.index_query.document_classes配置项，用于查询结果反序列化（比如我们查询的文档对应的实体类为Book，而Book对应的实体类被配置在了smart_search_box.index_query.document_classes的第一项，则此时documentType值为1）。
用户每次的搜索行为都会对关键词提示的优先级产生影响。

```shell
curl -X POST http://localhost:8080/search_box/mediiiia/search_document -d '{"documentType":0,"fields":["title", "content"],"index":1,"keyword":"白夜行","size":3}'

{
    "code": 0,
    "message": "正常响应",
    "results": [
        {
            "document": {
                "id": 21,
                "author": "东野圭吾",
                "title": "白夜行",
                "content": "《白夜行》是日本作家东野圭吾创作的长篇小说"
            },
            "score": 0.32716757
        }
    ],
    "pagination": {
        "size": 3,
        "index": 1
    }
}
```

**/search_box/timing/clean_up/document_zset**
这个端点用于启动一个任务，根据文档得分对文档的搜索提示优先级进行重排序，因为负载较重建议配合crontab配置定时任务在访问低峰期进行。由于使用分布式锁保证了幂等可以在分布式环境下使用。
```shell
crontab -e

# 每天凌晨三点执行
0 3 * * ? curl /search_box/timing/clean_up/document_zset
```

**5 用户自定义组件**
用户可以使用自定义的分词器，redis客户端和elk客户端(实际上可以替换为任何全文搜索引擎)，下面以自定义分词器为例子介绍如何替换框架的默认实现

实现 com.tinysakura.smartsearchbox.service包下的AnalyzerService接口（其它两个组件的服务接口也在该包下)
```java
public interface AnalyzerService {
    String[] analyzer(String originalText);
}

@Deprecated
public class IkAnalyzerAdapter implements AnalyzerService {
    Analyzer analyzer;

    /**
     * @param useSmart 是否使用ik_smart（相比较ik_max_word分词粒度较粗）
     */
    public IkAnalyzerAdapter(boolean useSmart) {
        this.analyzer = new IKAnalyzer(true);
    }

    @Override
    public String[] analyzer(String originalText) {
        StringReader reader=new StringReader(originalText);
        List<String> results = new ArrayList<>();
        TokenStream ts = null;

        //分词
        try {
            ts=analyzer.tokenStream("", reader);
            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
            //遍历分词数据
            while(ts.incrementToken()){
                results.add(term.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
            try {
                assert ts != null;
                ts.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results.toArray(new String[]{});
    }
}
```
可以看到这里使用了开源的IkAnalyzer作为分词实现，接下来就是如何使用它替换默认的Ansj分词器。
首先排除默认的LaunchConfiguration
```
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {LaunchConfiguration.class}))
```
然后使用我们自定义的config类进行注册bean，如果要进行其他组件的替换也同理。
```java
@Configuration
@Import({com.tinysakura.smartsearchbox.config.RedisConfiguration.class, com.tinysakura.smartsearchbox.config.RedissionConfiguration.class})
public class CustomConfiguration {
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
        Launch launch = new Launch(endPointProp(), indexInitProp(), searchPromptProp(), ikAnalyzerAdapter(), smartElkClientAdapter(), jedisClientAdapter(), redissonClient);

        return launch;
    }

    @Bean
    public AnalyzerService ikAnalyzerAdapter() {
        IkAnalyzerAdapter ikAnalyzerAdapter = new IkAnalyzerAdapter(false);

        return ikAnalyzerAdapter;
    }

//    @Bean
//    public AnalyzerService  ansjAnalyzerAdapter() {
//        AnsjAnalyzerAdapter ansjAnalyzerAdapter = new AnsjAnalyzerAdapter();
//
//        return ansjAnalyzerAdapter;
//    }

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
```

### others
1. 注意你的elk版本，如果使用框架默认的elk java client请尽量保证elk版本为5.6，如果使用自定义的elk client则需要保证客户端与elk版本保持匹配。
2. 项目暂时只整合了spring boot框架。若在原生spring中使用需要修改相应配置。
3. 客户端支持多节点的elk，若使用默认的elk java client，推荐将查询主节点和响应性高的节点配置在非主节点和低响应性的节点之前，这样可以提高部分性能。
4. [项目源码](https://github.com/Tinysakura/smart_search_box/branches)欢迎大家的star

### prospect
此版本为0.0.1-RELEASE版本，在1.0.0-RELEASE版本预计会增加如下功能：
1. 敏感词过滤
2. 文档索引条件表达式支持
3. 支持用户自定义查询模式
4. 提高/search_box/timing/clean_up/document_zset端点的性能
