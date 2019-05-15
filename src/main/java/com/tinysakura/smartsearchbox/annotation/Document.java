package com.tinysakura.smartsearchbox.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档映射注解
 * 类级注解，使用在dao层对应的原始bean类上
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
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