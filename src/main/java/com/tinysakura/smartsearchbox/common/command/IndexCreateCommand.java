package com.tinysakura.smartsearchbox.common.command;

import com.tinysakura.bean.index.Index;
import lombok.Data;

/**
 * 使用命令设计模式，封装创建索引命令
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */
@Data
public class IndexCreateCommand {
    private String indexName;

    private Index index;

    private String documentType;

    private String[] searchPromptFields;
}