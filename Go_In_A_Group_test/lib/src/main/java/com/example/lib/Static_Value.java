package com.example.lib;

import java.net.Socket;
import java.util.ArrayList;

//个人用户数据
public class Static_Value {
    public ID_Password you;
    public ArrayList<Group> yourGroup;

    public Static_Value(ID_Password id_password){
        you = id_password;
        yourGroup = new ArrayList<>();
    }
}
