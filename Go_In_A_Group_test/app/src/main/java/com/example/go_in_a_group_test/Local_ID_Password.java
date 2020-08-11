package com.example.go_in_a_group_test;

//这个类用来装载单个用户的信息
public class Local_ID_Password {
    private int ID;
    private String Password;
    public String Name;

    public Local_ID_Password(int ID, String Password, String name){
        this.ID = ID;
        this.Password = Password;
        this.Name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
}
