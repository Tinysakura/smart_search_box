package com.tinysakura.smartsearchbox.bean.document;

import com.tinysakura.smartsearchbox.constant.DocumentPropertiesConstant;
import lombok.Data;

/**
 * 对文档单个字段的描述
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/2
 */
@Data
public class Properties {
    /**
     * 公有属性
     */

    /**
     * 字段类型{@link DocumentPropertiesConstant.Type}
     * 数值类型特有的Type值{@link DocumentPropertiesConstant.Number.Type}
     */
    private String type;

    /**
     * 指定该字段的原始值是否会被写入索引{@link DocumentPropertiesConstant.Store}
     */
    private String store;

    /**
     * 指定该字段是否被写入索引以供搜索{@link DocumentPropertiesConstant.Index}
     */
    private String index;

    /**
     * 指定字段写入索引后的名称，若不指定则使用对象的原始名称
     */
    private String index_name;

    /**
     * 定义字段在文档的重要性，数值越高重要性越高
     */
    private double boost;

    /**
     * =============================================
     */

    /**
     * 字符串类型特有属性
     */

    /**
     * 定义用于该字段的索引和搜索的分析器，若不指定则默认使用全局定义的分析器
     */
    private String analyzer;


    /**
     * =============================================
     */

    /**
     * 日期类型特有属性
     */

    /**
     * 指定日期格式，默认值为dateOptionalTime
     */
    private String format;

}