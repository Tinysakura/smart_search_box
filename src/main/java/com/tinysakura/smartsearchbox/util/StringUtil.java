package com.tinysakura.smartsearchbox.util;

import com.tinysakura.smartsearchbox.common.command.DocumentAddCommand;
import com.tinysakura.smartsearchbox.common.command.IndexCreateCommand;

import java.util.Arrays;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/12
 */

public class StringUtil {
    /**
     * {index}_document_{key}
     */
    private static final String DOCUMENT_ZSET_KEY_FORMAT = "%s_documentZSet_%s";

    /**
     * {index}_behavior_{key}
     */
    private static final String BEHAVIOR_ZSET_KEY_FORMAT = "%s_behaviorZSet_%s";

    /**
     * 首字母转小写
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 首字母转大写
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 从key值中提取搜索提示字段信息
     * @param key
     * @return
     */
    public static String[] extractFieldsFromKey(String key) {
        String[] splits = key.split("_");

        return Arrays.copyOfRange(splits, 2, splits.length);
    }

    /**
     * 从key值中提取索引信息
     * @param key
     * @return
     */
    public static String extractIndexNameFromKey(String key) {
        String[] splits = key.split("_");

        return splits[0];
    }

    public static String documentSetKey(DocumentAddCommand documentAddCommand) {
        // keyFormat : {索引名}_{文档类型set后缀}_{搜索提示字段1}_{搜索提示字段2}...
        StringBuilder sb = new StringBuilder();
        sb.append(documentAddCommand.getIndex()).append("_");
        sb.append("documentSet");

        for (String field : documentAddCommand.getSearchPromptFields()) {
            sb.append("_").append(field);
        }

        return sb.toString();
    }

    public static String documentSetKey(IndexCreateCommand indexCreateCommand) {
        // keyFormat : {索引名}_{文档类型set后缀}_{搜索提示字段1}_{搜索提示字段2}...
        StringBuilder sb = new StringBuilder();
        sb.append(indexCreateCommand.getIndexName()).append("_");
        sb.append("documentSet");

        for (String field : indexCreateCommand.getSearchPromptFields()) {
            sb.append("_").append(field);
        }

        return sb.toString();
    }

    public static String behaviorSetKey(String index) {
        StringBuilder sb = new StringBuilder();
        sb.append(index).append("_");
        sb.append("behaviorSet");

        return sb.toString();
    }

    public static String behaviorZSetKey(String index, String key) {
        return String.format(BEHAVIOR_ZSET_KEY_FORMAT, index, key);
    }

    /**
     * 构建指定索引下的zSet key值
     * @param index
     * @param key
     * @return
     */
    public static String documentZSetKey(String index, String key) {
        return String.format(DOCUMENT_ZSET_KEY_FORMAT, index, key);
    }

    public static String extractKeywordFromDocumentZSetKey(String documentZSetkey) {
        return documentZSetkey.split("_")[2];
    }
}