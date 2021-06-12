package com.dzenm;

import org.litepal.crud.LitePalSupport;

public class Username extends LitePalSupport {

    private long id;
    private String username;
    private String sex;
    private int age;
    private String address;

    public Username(long id, String username, String sex, int age, String address) {
        this.id = id;
        this.username = username;
        this.sex = sex;
        this.age = age;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
