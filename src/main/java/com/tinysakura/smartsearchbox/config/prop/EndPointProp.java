package com.tinysakura.smartsearchbox.config.prop;

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
public class EndPointProp {

    private String sensitiveWord;

    private String[] sensitiveWords;
}