package com.example.lib;

public class ID_Password {
    private int ID;
    private String Password;

    public ID_Password(int ID, String Password){
        this.ID = ID;
        this.Password = Password;
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
