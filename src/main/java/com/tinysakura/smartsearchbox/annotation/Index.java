package com.tinysakura.smartsearchbox.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档索引注解
 * 方法级注解，在dao层接口的save方法上使用
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * dao层bean在ioc容器中的bean name
     * @return
     */
    public String beanName() default "";

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
     * 指定文档索引表达式，只有符合表达式要求的文档最终才会被索引
     * @return
     */
    public String indexExpression() default "";

    /**
     * 指定搜索提示字段
     * @return
     */
    public String prompt() default "";

    /**
     * 指定文档搜索提示表达式，用于计算推荐分
     * @return
     */
    public String promptExpression() default "";


}