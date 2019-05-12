package com.tinysakura.smartsearchbox.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档字段映射注解
 * 字段级注解，使用在dao层对应的原始bean类需要被存储在索引的字段上
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
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