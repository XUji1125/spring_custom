package com.yunqia.demo.service.impl;

public class TestDemo {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String test(String mes) {
        System.out.println("调用成功： " + mes);
        return mes;
    }

}
