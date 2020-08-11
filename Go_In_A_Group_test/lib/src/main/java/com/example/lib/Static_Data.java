package com.example.lib;

import java.util.ArrayList;

//服务器维护的所有用户的信息
public class Static_Data {
    public static ArrayList<Static_Value> Datas = new ArrayList<>();//所有用户的实时信息的表单
    public static ArrayList<Group> group = new ArrayList<>();//所有存在的群的信息
    public static ArrayList<ID_Password> id_password = new ArrayList<>();//已存在的用户账户信息
}
