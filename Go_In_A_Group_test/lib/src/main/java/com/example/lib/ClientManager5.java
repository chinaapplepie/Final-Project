package com.example.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientManager5 {
    //Map集合用于储存元素对，Map储存的是一对键值（key和value），是通过key映射到它的value；
    private static Map<String, Socket> clientList = new HashMap<>();
    //ServerThread类（自定义的真正用于多线程实现服务器端的类）
    private static ServerThread serverThread = null;

    //Runnable是Thread的接口。实现Runnable接口实现多线程。
    private static class ServerThread implements Runnable {
        public int port;
        private boolean isExit = false;
        private ServerSocket server;
        public ArrayList<Group> return_Group = new ArrayList<>();

        //构造方法
        public ServerThread() {
        }

        public ServerThread(int port) {
            this.port = port;
            try {
                server = new ServerSocket(port);//创建绑定到特定端口的服务器套接字。
                System.out.println("启动服务成功" + "port:" + port);
            } catch (IOException e) {
                System.out.println("启动server失败，错误原因：" + e.getMessage());
            }
        }

        //启动线程为当前的连接服务
        @Override
        public void run() {
            if(port == 10015){
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
                                        System.out.println("收到的地址为：" + text);
                                        String[] split = (text).split("//");
                                        //判断服务为Search
                                        if(split[0].equals("GetGroup")){
                                            int temp = Integer.valueOf(split[1]);
                                            OutputStream outputStream = socket.getOutputStream();
                                            String m = "GetGroup";
                                            for(int i=0; i <Static_Data.Datas.size(); i++) {
                                                if (Static_Data.Datas.get(i).you.getID() == temp) {
                                                    if(Static_Data.Datas.get(i).yourGroup.size()>0){
                                                        for(int j = 0; j<Static_Data.Datas.get(i).yourGroup.size(); j++){
                                                            m=m+"&&";
                                                            m=m+Static_Data.Datas.get(i).yourGroup.get(j).Build_String();
                                                        }
                                                        System.out.println("返回字符串："+m);
                                                    }
                                                }
                                            }
                                            outputStream.write(m.getBytes("utf-8"));
                                            outputStream.flush();
                                        }
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
