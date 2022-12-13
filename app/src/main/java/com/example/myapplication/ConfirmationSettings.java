package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ConfirmationSettings extends AppCompatActivity {

    private EarthquakesParams eqparams;
    private TextView text;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd   HH mm ss", Locale.ENGLISH);
    private Date currentDate = new Date();

    private ArrayList<String> list = new ArrayList<String>();

    private Earthquake eq;

    private int notificationID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_settings);
        createNotificationChannel();

        if(!getIntent().getSerializableExtra("earthquake_parameter").equals(null)) {
            eqparams = (EarthquakesParams) getIntent().getSerializableExtra("earthquake_parameter");

            text = (TextView) findViewById(R.id.confirm_params);
            text.setText(eqparams.toString());

            new readData().start();

        }else{
            text = (TextView) findViewById(R.id.confirm_params);
            text.setText("Couldn't load parameters");
        }



    }

    private void sendNotification(Earthquake eq,int notificationID){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ConfirmationSettings.this, "Earthquake Notification")
                .setSmallIcon(R.drawable.eq_warning)
                .setContentTitle("Earthquake")
                .setContentText(eq.toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);


        Intent intent = new Intent(this, EarthquakeMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("earthquake",eq);
        PendingIntent pendingIntent =PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ConfirmationSettings.this);
        managerCompat.notify(notificationID, builder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Earthquake Notification";
            String description = "Earthquake Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Earthquake Notification", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 });
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    class readData extends Thread{

        @Override
        public void run() {

            while (true) {

                try {
                    URL u = new URL("https://bbnet2.gein.noa.gr/current_catalogue/current_catalogue_year2.php");
                    URLConnection c = u.openConnection();
                    InputStream r = c.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(r));

                    //BufferedReader reader = new BufferedReader(new FileReader("eq_test.txt"));
                    for (String line; (line = reader.readLine()) != null;) {
                        //System.out.println(line.substring(1, 23));
                        //System.out.println(line.substring(26, 33));
                        //System.out.println(line.substring(34, 41));
                        //System.out.println(line.substring(43, 46));
                        //System.out.println(line.substring(55, 58));
                        //System.out.println("-----------------------------------");

                        list.add(line);
                        //System.out.println(line);
                    }


                    Collections.reverse(list);
                    list.remove(0);



                    for (int i=0; i<list.size(); i++) {

                        String currLine= list.get(i);
                        System.out.println(currLine);
                        //System.out.println(currLine.substring(1, 23));
                        Date firstDate = currentDate;
                        Date secondDate = sdf.parse(currLine.substring(1, 23));

                        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                        long diffMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);


                        if (diffMinutes >= eqparams.getNotification()) {
                            //System.out.println("Nothing");
                            break;
                        } else {

                            eq = new Earthquake(sdf.parse(currLine.substring(1, 23)),currLine.substring(1, 23), currLine.substring(26, 33), currLine.substring(34, 41), currLine.substring(43, 46), currLine.substring(55, 58));

                            if (distance(eqparams.getLatitude(), eq.getLattitude(), eqparams.getLongtitude(), eq.getLongtitude()) < eqparams.getRange()) {

                                if (eqparams.getMagnitude() < eq.getMagnitude()) {
                                    //System.out.println(eq);
                                    sendNotification(eq,notificationID++);
                                }

                            }

                        }

                        //System.out.println(currLine.substring(1, 23) + "  Minutes difference:" + diffMinutes);

                        list.clear();
                        Thread.sleep(eqparams.getNotification() * 1000 * 60);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }


            }

        }
        public double distance(double lat1, double lat2, double lon1, double lon2) {

            // The math module contains a function
            // named toRadians which converts from
            // degrees to radians.
            lon1 = Math.toRadians(lon1);
            lon2 = Math.toRadians(lon2);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            // Haversine formula
            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double a = Math.pow(Math.sin(dlat / 2), 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.pow(Math.sin(dlon / 2), 2);

            double c = 2 * Math.asin(Math.sqrt(a));

            // Radius of earth in kilometers.
            double r = 6371;

            // calculate the result
            return (c * r);
        }


    }



}