package com.dzenm.naughty.db.model;

import androidx.annotation.NonNull;

/**
 * 数据库表列
 * {cid: 0, name: id, type: INTEGER, notnull: 1, dflt_value: null, pk: 1}
 * {cid: 1, name: desc, type: TEXT, notnull: 0, dflt_value: null, pk: 0}
 */
public class Column {

    private int cid;
    private String name;
    private String type;
    private int notnull;
    private String dflt_value;
    private int pk;
    private int width = 100;

    public Column(int cid, String name, String type, int notnull, String dflt_value, int pk) {
        this.cid = cid;
        this.name = name;
        this.type = type;
        this.notnull = notnull;
        this.dflt_value = dflt_value;
        this.pk = pk;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNotnull() {
        return notnull;
    }

    public void setNotnull(int notnull) {
        this.notnull = notnull;
    }

    public String getDflt_value() {
        return dflt_value;
    }

    public void setDflt_value(String dflt_value) {
        this.dflt_value = dflt_value;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @NonNull
    @Override
    public String toString() {
        return "Column{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", notnull=" + notnull +
                ", dflt_value='" + dflt_value + '\'' +
                ", pk=" + pk +
                '}';
    }
}
