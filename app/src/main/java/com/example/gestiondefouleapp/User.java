package com.example.gestiondefouleapp;

public class User {
    private String mName;
    private String mAddress;
    private String mphone;

    public User(String name, String address,String phone) {
        mName = name;

        mAddress = address;
        mphone=phone;

    }
    public User() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhone() {
        return mphone;
    }

    public void setPhone(String phone) {
        mphone = phone;
    }
}
