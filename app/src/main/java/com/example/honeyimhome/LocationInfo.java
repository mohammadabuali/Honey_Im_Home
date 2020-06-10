package com.example.honeyimhome;

public class LocationInfo {
    public double longitude;
    public double latitude;
    public double accuracy;
    LocationInfo(double lon, double lat, double acc){
        this.latitude = lat;
        this.longitude = lon;
        this.accuracy = acc;
    }
}
