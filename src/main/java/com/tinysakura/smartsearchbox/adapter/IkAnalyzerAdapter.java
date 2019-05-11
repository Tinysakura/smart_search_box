package com.tinysakura.smartsearchbox.adapter;

import com.tinysakura.smartsearchbox.service.AnalyzerService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用开源的中文分词器IK Analyzer提供分词服务
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/11
 */

public class IkAnalyzerAdapter implements AnalyzerService {
    Analyzer analyzer;

    /**
     * @param useSmart 是否使用ik_smart（相比较ik_max_word分词粒度较粗）
     */
    public IkAnalyzerAdapter(boolean useSmart) {
        this.analyzer = new IKAnalyzer(true);
    }

    @Override
    public String[] analyzer(String originalText) {
        StringReader reader=new StringReader(originalText);
        List<String> results = new ArrayList<>();

        //分词
        try {
            TokenStream ts=analyzer.tokenStream("", reader);
            CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
            //遍历分词数据
            while(ts.incrementToken()){
                results.add(term.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }

        return results.toArray(new String[]{});
    }
}