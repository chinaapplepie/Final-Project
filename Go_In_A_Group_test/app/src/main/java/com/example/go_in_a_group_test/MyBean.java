package com.example.go_in_a_group_test;

//用来记录名字，消息内容，消息时间，以及加载哪种布局
public class MyBean {
    private String data;
    private String time;
    private String name;
    private int number;//哪一种样式

    //自定义构造方法
    public MyBean(String data, int number,String time,String name) {
        this.data = data;
        this.number = number;
        this.name = name;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
