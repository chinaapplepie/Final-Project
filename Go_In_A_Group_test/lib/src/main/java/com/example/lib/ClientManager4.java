package com.example.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientManager4 {
    //Map集合用于储存元素对，Map储存的是一对键值（key和value），是通过key映射到它的value；
    private static Map<String,Socket> clientList = new HashMap<>();
    //ServerThread类（自定义的真正用于多线程实现服务器端的类）
    private static ServerThread serverThread = null;

    //Runnable是Thread的接口。实现Runnable接口实现多线程。
    private static class ServerThread implements Runnable {
        public int port;
        private boolean isExit = false;
        private ServerSocket server;
        private String s;
        public ArrayList<Group> return_Group = new ArrayList<>();

        //构造方法
        public ServerThread() {
        }

        public ServerThread(int port) {
            s="False";

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
            if(port == 10014){
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
                                        if(split[0].equals("Search")){
                                            OutputStream outputStream = socket.getOutputStream();
                                            for(int i = 0 ; i < Static_Data.group.size() ; i++){
                                                String temp_Lantitude = Static_Data.group.get(i).getLatitude();
                                                String temp_Longitude = Static_Data.group.get(i).getLongitude();
                                                double  distance = GetDistance(Double.parseDouble(split[1]),Double.parseDouble(split[2]),
                                                        Double.parseDouble(temp_Lantitude),Double.parseDouble(temp_Longitude));
                                                System.out.println("距离为："+Double.toString(distance));
                                                if(distance <= 1.0){
                                                    //距离在一公里以内
                                                    return_Group.add(Static_Data.group.get(i));
                                                }
                                            }
                                            String m = "Search";
                                            if(return_Group.size()>0){
                                                for(int i = 0; i<return_Group.size(); i++){
                                                    m=m+"&&";
                                                    m=m+return_Group.get(i).Build_String();
                                                }
                                                System.out.println("返回字符串："+m);
                                                return_Group.clear();
                                            }
                                            outputStream.write(m.getBytes("utf-8"));
                                            outputStream.flush();
                                        }
                                        //判断服务是加入群聊
                                        else if(split[0].equals("Join")){
                                            OutputStream outputStream = socket.getOutputStream();
                                            String n = "Join";
                                            boolean flag = false;//不存在相同用户
                                            int temp_id = Integer.valueOf(split[1]);//想要加入的群的id
                                            int temp = Integer.valueOf(split[2]);//想要加群的用户的id
                                            for(int i = 0 ; i < Static_Data.group.size() ; i++){
                                                if(temp_id == Static_Data.group.get(i).getID()){//找到这个群
                                                    for(int j = 0; j < Static_Data.group.get(i).List.size();j++){//遍历群里用户
                                                        if(Static_Data.group.get(i).List.get(j).getID() == temp){//用户在群里
                                                            flag = true;
                                                        }
                                                    }
                                                    if(flag == true){
                                                        n = "Join"+"&&"+"False_Exist";
                                                    }else{
                                                        for(int j=0; j <Static_Data.Datas.size(); j++) {
                                                            if (Static_Data.Datas.get(j).you.getID() == temp) {//找到你自己的群表序号j
                                                                Static_Data.group.get(i).add(Static_Data.Datas.get(j).you);//更新系统群表
                                                                Static_Data.Datas.get(j).yourGroup.add(Static_Data.group.get(i));//更新自己的群表
                                                                n = "Join"+"&&"+"True";
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if(n.equals("Join")){//n在遍历过程中都没有发生改变
                                                n = "Join"+"&&"+"False_No";
                                            }
                                            System.out.println("返回字符串："+n);
                                            outputStream.write(n.getBytes("utf-8"));
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


    //计算地球两点之间距离的函数
    private static double EARTH_RADIUS = 6378.137;//地球半径
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
