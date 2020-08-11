package com.example.lib;

public class MyClass {
    public static void main(String[]args) {
        // 开启服务器
        ClientManager.startServer(10011);
        ClientManager2.startServer(10010);
        ClientManager3.startServer(10012);
        ClientManager4.startServer(10014);
        ClientManager5.startServer(10015);
    }
}
