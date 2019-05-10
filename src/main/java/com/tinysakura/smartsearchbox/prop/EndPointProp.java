package com.tinysakura.smartsearchbox.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 搜索提示端点与文档查询端点相关配置
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Data
@Configuration
public class EndPointProp {

    @Value("smart_search_box.search_prompt.sensitive_word")
    private String sensitiveWord;

    private String[] sensitiveWords;

    @Bean
    public EndPointProp endPointProp() {
        EndPointProp endPointProp = new EndPointProp();
        endPointProp.setSensitiveWord(this.sensitiveWord);
        String[] splits = sensitiveWord.split(",");
        endPointProp.setSensitiveWords(splits);

        return endPointProp;
    }
}