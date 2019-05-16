package com.tinysakura.smartsearchbox.adapter;

import com.tinysakura.smartsearchbox.service.AnalyzerService;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.IndexAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/16
 */

public class AnsjAnalyzerAdapter implements AnalyzerService {

    @Override
    public String[] analyzer(String originalText) {
        Result parse = IndexAnalysis.parse(originalText);
        List<String> resultList = new ArrayList<>();

        parse.forEach(e -> {
            resultList.add(e.toString().split("/")[0]);
        });

        return resultList.toArray(new String[]{});
    }
}