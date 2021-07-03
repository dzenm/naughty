package com.dzenm;

import org.litepal.crud.LitePalSupport;

public class Username extends LitePalSupport {

    private long id;
    private String username;
    private String sex;
    private int age;
    private String address;
    private boolean isRoot;
    private long date;
    private float amount;
    private double sum;

    public Username(long id, String username, String sex, int age, String address, boolean isRoot, long date, float amount, double sum) {
        this.id = id;
        this.username = username;
        this.sex = sex;
        this.age = age;
        this.address = address;
        this.isRoot = isRoot;
        this.date = date;
        this.amount = amount;
        this.sum = sum;
    }
}
