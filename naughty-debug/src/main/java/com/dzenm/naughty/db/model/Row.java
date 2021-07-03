package com.dzenm.naughty.db.model;

/**
 * 数据列结构（键值对的形式）
 */
public class Row {

    private String key;
    private Object value;

    public Row(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
