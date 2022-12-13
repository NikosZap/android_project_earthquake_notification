package com.example.myapplication;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Earthquake implements Serializable {

    private Date datetime;
    private String dateTimeStr;
    private double lattitude;
    private double longtitude;
    private String depth;
    private double magnitude;
    private SimpleDateFormat sdf1 =new SimpleDateFormat("dd MMM yyyy HH mm ss", Locale.ENGLISH);
    private SimpleDateFormat sdf =new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH);




    Earthquake(Date datetime, String datetimeStr, String lattitudeStr, String longtitudeStr, String depthStr, String magnitudeStr) throws ParseException{



        this.datetime=datetime;
        this.dateTimeStr=datetimeStr;
        this.depth=depthStr.replace(" ", "");
        this.lattitude=Double.parseDouble(lattitudeStr);
        this.longtitude=Double.parseDouble(longtitudeStr);
        this.magnitude=Double.parseDouble(magnitudeStr);



    }

    public double getLattitude(){
        return lattitude;
    }

    public double getLongtitude(){
        return longtitude;
    }
    public double getMagnitude(){
        return magnitude;
    }

    @Override
    public String toString(){
        return magnitude+" magn, "+sdf.format(datetime)+ ", " + depth+" km depth.\n";
    }
}