package com.example.gestiondefouleapp;

public class HistoryItem {
    //img
    private int mimgid;
    // TextView 1
    private String mTime;

    // TextView 1
    private String mDate;
    // TextView 1
    //private String mAdress;

    public HistoryItem() {
    }

    // create constructor to set the values for all the parameters of the each single view
    public HistoryItem( int imgid,String Time, String Date) {

        mTime = Time;
        mDate = Date;
        //mAdress=Adress;
        mimgid =imgid;

    }
    // getter method for returning the ID of the imageview
    public int getImageId() {
        return mimgid;
    }

    // getter method for returning the ID of the TextView 1
    public String getTime() {
        return mTime;
    }

    // getter method for returning the ID of the TextView 2
    public String getDate() {
        return mDate;
    }
    // getter method for returning the Adress of the TextView 2
    /*public String getAdress() {
        return mAdress;
    }*/
}
