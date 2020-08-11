package com.example.go_in_a_group_test;

import java.util.ArrayList;
import java.util.List;

//这个类用来装载从服务器端下载下来的群对象
public class Local_Group {
    private int ID;
    private String Centre;
    private String Longitude;
    private String Latitude;
    public ArrayList<String> Name;
    private int Count;


    public Local_Group(int id, String centre, String latitude, String longitude, int count){
        ID = id;
        Centre = centre;
        Latitude = latitude;
        Longitude = longitude;
        Count = count;
        Name = new ArrayList<>();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCentre() {
        return Centre;
    }

    public void setCentre(String centre) {
        Centre = centre;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
