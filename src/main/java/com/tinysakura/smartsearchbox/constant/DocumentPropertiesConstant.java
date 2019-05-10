package com.tinysakura.smartsearchbox.constant;

/**
 * 文档字段属性相关的Constant类
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/2
 */

public class DocumentPropertiesConstant {
    public class Type {
        public static final String TEXT = "text";

        public static final String NUMBER = "number";

        public static final String DATE = "date";

        public static final String BOOLEAN = "boolean";

        public static final String BINARY = "binary";

        /**
         * ip地址类型
         */
        public static final String IP = "ip";

        /**
         * 存储在索引中的的是字段的字数信息而不是原始文本，允许接受与number类型相同的配置选项
         */
        public static final String TOKEN_COUNT = "token_count";
    }

    public class Store {
        public static final String YES = "yes";

        public static final String NO = "no";
    }

    public class Index {
        public static final String ANALYZED = "analyzed";

        public static final String NOT_ANALYZED = "no";
    }

    public class Strings {
        class TermVector {
            public static final String NO = "no";

            public static final String YES = "yes";

            public static final String WITH_OFFSETS = "with_offsets";

            public static final String WITH_POSITIONS = "with_positions";

            public static final String WITH_POSITIONS_OFFSETS = "with_positions_offsets";
        }
    }

    public class Number {
        class Type {
            private static final String BYTE = "byte";

            private static final String SHORT = "short";

            private static final String INTEGER = "integer";

            private static final String LONG = "long";

            private static final String FLOAT = "float";

            private static final String DOUBLE = "double";
        }
    }
}