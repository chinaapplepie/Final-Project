package com.example.lib;

import java.util.ArrayList;

public class Group {
    private int ID;
    private String Centre;
    private String Latitude;
    private String Longitude;
    public ArrayList<ID_Password> List;
    public int count;//群里的人数

    public Group(int id, String centre,  String latitude ,String longitude){
        ID = id;
        Centre = centre;
        Latitude = latitude;
        Longitude = longitude;
        List = new ArrayList<>();
        count=0;
    }

    //群里加入人员
    public void add(ID_Password id_password){
        List.add(id_password);
        count++;
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

    public String Build_String(){
        if(count == 1){
            return String.valueOf(ID)+"//"+Centre+"//"+Latitude+"//"+Longitude+"//"+List.get(0).Name+"//"+
                    "null"+"//"+"null"+"//"+"null"+"//"+String.valueOf(count);
        }
        else if(count == 2){
            return String.valueOf(ID)+"//"+Centre+"//"+Latitude+"//"+Longitude+"//"+List.get(0).Name+"//"+
                    List.get(1).Name+"//"+"null"+"//"+"null"+"//"+String.valueOf(count);
        }
        else if(count == 3){
            return String.valueOf(ID)+"//"+Centre+"//"+Latitude+"//"+Longitude+"//"+List.get(0).Name+"//"+
                    List.get(1).Name+"//"+List.get(2).Name+"//"+"null"+"//"+String.valueOf(count);
        }
        else if(count == 4){
            return String.valueOf(ID)+"//"+Centre+"//"+Latitude+"//"+Longitude+"//"+List.get(0).Name+"//"+
                    List.get(1).Name+"//"+List.get(2).Name+"//"+List.get(3).Name+"//"+String.valueOf(count);
        }
        else{
            return null;
        }
    }
}
