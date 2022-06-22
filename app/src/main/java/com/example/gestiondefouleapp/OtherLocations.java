package com.example.gestiondefouleapp;

public class OtherLocations {
    public Double longitude;
    public Double latitude;

    public OtherLocations() {

    }

    public OtherLocations(Double longitude, Double latitude) {
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
