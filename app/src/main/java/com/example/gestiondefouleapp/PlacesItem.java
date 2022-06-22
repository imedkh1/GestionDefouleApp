package com.example.gestiondefouleapp;

public class PlacesItem {
    private String mname;


    private int mstate;

    //private String mAdress;
    public PlacesItem() {

    }
    // create constructor to set the values for all the parameters of the each single view
    public PlacesItem(String name, int state) {

        mname = name;
        mstate = state;
        //mAdress=Adress;
        // mimgid =imgid;

    }

    // getter method for returning the ID of the TextView 1
    public String getName() {
        return mname;
    }

    // getter method for returning the ID of the TextView 2
    public int getState() {
        return mstate;
    }

}
