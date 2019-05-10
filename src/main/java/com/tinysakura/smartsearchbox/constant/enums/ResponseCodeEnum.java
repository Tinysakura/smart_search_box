package com.tinysakura.smartsearchbox.constant.enums;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/10
 */
public enum ResponseCodeEnum {
    /**
     * 正常响应
     */
    OK(0, "正常响应"),
    /**
     * 服务器内部错误
     */
    ERROR(-1, "服务器内部错误");

    private Integer code;
    private String value;

    ResponseCodeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
