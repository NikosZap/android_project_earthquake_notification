package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class EarthquakesParams extends AppCompatActivity implements Serializable {

   private int maxRange;
   private int minNotif;
   private double minMagnitude;
   private double lat,lng;

   EarthquakesParams(){ //default constr
       maxRange=1000;
       minNotif=60;
       minMagnitude=0.1;
       lat =37.983810; // Athens Coordinates
       lng=23.727539;

       //createNotificationChannel();
   }


    public void setLocation(double lat,double lng) {
        this.lat=lat;
        this.lng=lng;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public void setMagnitude(double minMagnitude) {
        this.minMagnitude = minMagnitude;
    }

    public void setMinNotif(int minNotif) {
        this.minNotif = minNotif;
    }

    public double getLatitude(){
        return lat;
    }

    public double getLongtitude(){
        return lng;
    }
    public double getMagnitude(){
        return minMagnitude;
    }
    public int getNotification(){
        return minNotif;
    }
    public int getRange(){
       return maxRange;
    }


    @Override
    public String toString(){
        return "Coordinates: "+ lat+", "+ lng+"\nRange: "+maxRange+" km"+ "\nNotification: "+minNotif+" min\nMagnitude: "+minMagnitude;
    }

}
