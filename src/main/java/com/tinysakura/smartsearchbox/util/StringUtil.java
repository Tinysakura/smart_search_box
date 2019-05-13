package com.tinysakura.smartsearchbox.util;

import java.util.Arrays;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/12
 */

public class StringUtil {

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

        return Arrays.copyOfRange(splits, 2, splits.length - 1);
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
}