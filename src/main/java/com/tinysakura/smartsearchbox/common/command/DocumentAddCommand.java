package com.tinysakura.smartsearchbox.common.command;

import lombok.Data;

/**
 * 使用命令设计模式，封装索引文档命令
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */
@Data
public class DocumentAddCommand {
    String index;

    String documentType;

    Object document;

    String[] searchPromptFields;
}