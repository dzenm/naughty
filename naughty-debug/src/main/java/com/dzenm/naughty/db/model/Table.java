package com.dzenm.naughty.db.model;

import androidx.annotation.NonNull;

/**
 * 查询表结构
 * {type: table, name: android_metadata, tbl_name: android_metadata, rootpage: 3, sql: CREATE TABLE android_metadata (locale TEXT)}
 */
public class Table {

    private String type;
    private String name;
    private String tbl_name;
    private int rootpage;
    private String sql;

    public Table(String type, String name, String tbl_name, int rootpage, String sql) {
        this.type = type;
        this.name = name;
        this.tbl_name = tbl_name;
        this.rootpage = rootpage;
        this.sql = sql;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTbl_name() {
        return tbl_name;
    }

    public void setTbl_name(String tbl_name) {
        this.tbl_name = tbl_name;
    }

    public int getRootpage() {
        return rootpage;
    }

    public void setRootpage(int rootpage) {
        this.rootpage = rootpage;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @NonNull
    @Override
    public String toString() {
        return "Table{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", tbl_name='" + tbl_name + '\'' +
                ", rootpage=" + rootpage +
                ", sql='" + sql + '\'' +
                '}';
    }
}
