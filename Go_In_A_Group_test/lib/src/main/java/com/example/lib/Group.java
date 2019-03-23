package com.example.lib;

import java.util.ArrayList;

public class Group {
    private int ID;
    private String Centre;
    private String Longitude;
    private String Latitude;
    public ArrayList<ID_Password> List;

    public Group(int id, String centre, String longitude, String latitude){
        ID = id;
        Centre = centre;
        Longitude = longitude;
        Latitude = latitude;
        List = new ArrayList<>();
    }

    public void add(ID_Password id_password){
        List.add(id_password);
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
}
