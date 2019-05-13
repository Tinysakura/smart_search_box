redis中数据结构

索引文档相关
第一层：set (key -> document) (value -> 第二层的key)
第二层：set (key -> {index}\_documentSet_{field1}\_{field2}\_...) (value -> 第三层的key)
第三层：zset (key -> {index}\_{documentZSet}\_{分词结果}) (value -> 搜索提示内容)

用户行为相关
第一层：set (key -> behavior) (value -> 第二层的key) 
第二层：set (key -> {index}\_behaviorSet) (value -> 第三层的key)
第三层：zset (key -> {index}\_{behaviorZSet}\_{分词结果}) (value -> 搜索提示内容)