package com.tinysakura.smartsearchbox.service;

/**
 * 分词能力接口，框架使用者可以使用不同的开源分词器实现该接口进行替换
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */

public interface AnalyzerService {

    String[] analyzer(String originalText);
}