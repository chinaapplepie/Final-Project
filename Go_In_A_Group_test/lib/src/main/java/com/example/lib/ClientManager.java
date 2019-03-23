package com.example.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//此服务器用来维护登陆
public class ClientManager {
    //Map集合用于储存元素对，Map储存的是一对键值（key和value），是通过key映射到它的value；
    private static Map<String,Socket> clientList = new HashMap<>();
    //ServerThread类（自定义的真正用于多线程实现服务器端的类）
    private static ServerThread serverThread = null;

    //Runnable是Thread的接口。实现Runnable接口实现多线程。
    private static class ServerThread implements Runnable {

        //利用ID_Password类进行数据库模拟操作
        private ArrayList<ID_Password> list;
        public int port;
        private boolean isExit = false;
        private ServerSocket server;
        private String s;

        //构造方法
        public ServerThread() {
        }

        public ServerThread(int port) {
            //数据存储的表单
            list = new ArrayList();
            SetList(list);

            s="False";

            this.port = port;
            try {
                server = new ServerSocket(port);//创建绑定到特定端口的服务器套接字。
                System.out.println("启动服务成功" + "port:" + port);
            } catch (IOException e) {
                System.out.println("启动server失败，错误原因：" + e.getMessage());
            }
        }

        //每次启动服务器时早已经存储好了用户名和密码
        public ArrayList<ID_Password> SetList(ArrayList<ID_Password> list){
            ID_Password user1 = new ID_Password(20161596,"090214");
            list.add(user1);
            return list;
        }

        //启动线程为当前的连接服务
        @Override
        public void run() {
            if(port == 10011){
                try {
                    while (!isExit) {
                        // 进入等待环节
                        System.out.println("等待手机的连接... ... ");
                        final Socket socket = server.accept();//侦听并接受到此套接字的连接。
                        // 获取手机连接的地址及端口号
                        final String address = socket.getRemoteSocketAddress().toString();
                        System.out.println("连接成功，连接的手机为：" + address);
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    // 定义输入流
                                    InputStream inputStream = socket.getInputStream();
                                    byte[] buffer = new byte[1024];
                                    int len;
                                    while ((len = inputStream.read(buffer)) != -1){
                                        String text = new String(buffer,0,len);
                                        System.out.println("收到的账号密码为：" + text);
                                        //验证账号密码是否正确
                                        String[] split = (text).split("//");
                                        OutputStream outputStream = socket.getOutputStream();
                                        for(int i = 0 ; i < list.size() ; i++){
                                            int temp = list.get(i).getID();
                                            String temp2 = list.get(i).getPassword();
                                            if(String.valueOf(temp).equals(split[0]) && temp2.equals(split[1])){
                                                s = "True";
                                            }
                                        }
                                        outputStream.write(s.getBytes("utf-8"));
                                        outputStream.flush();
                                        s = "False";
                                    }
                                }catch (Exception e){
                                    System.out.println("错误信息为：" + e.getMessage());
                                }
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //关闭套接字
        public void Stop(){
            isExit = true;
            if (server != null){
                try {
                    server.close();
                    System.out.println("已关闭server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //开启服务的方法
    public static ServerThread startServer(int port){
        System.out.println("开启服务");
        if (serverThread != null){
            showDown();
        }
        serverThread = new ServerThread(port);
        new Thread(serverThread).start();
        System.out.println("开启服务成功");
        return serverThread;
    }

    // 关闭所有server socket 和 清空Map
    public static void showDown(){
        for (Socket socket : clientList.values()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverThread.Stop();
        clientList.clear();
    }
}
