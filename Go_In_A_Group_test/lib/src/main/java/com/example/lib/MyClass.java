package com.example.lib;

public class MyClass {
    public static void main(String[]args) {
        // 开启服务器
        ClientManager.startServer(10011);
        ClientManager2.startServer(10010);
    }
}
