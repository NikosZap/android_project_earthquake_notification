package com.example.myapplication;

import static java.lang.Integer.parseInt;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity  {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int MAPS_ACTIVITY_REQUEST_CODE = 1999;

    private static final String TAG = "MainActivity";
    private Boolean mLocationPermissionGranted = false;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng defaultCoordParams= new LatLng(37.983810, 23.727539); //Athens center
    private LatLng coordParams=defaultCoordParams;

    private final int defMaxRange = 1000;
    private final double defMinMagnitude = 0.1;
    private final int defMinNotif = 1;
    private int range, notif;
    private double magnitude;


    private EarthquakesParams eqparams = new EarthquakesParams();
    private EditText Magn, Range, Notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isServicesOk()){
            init();
            getLocationPermission();

            eqparams.setLocation(defaultCoordParams.latitude,defaultCoordParams.longitude);
        }
    }

    public void init(){
        //createNotificationChannel();

        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("earthquake_parameter",eqparams);
                //startActivity(intent);
                startActivityIfNeeded(intent,MAPS_ACTIVITY_REQUEST_CODE);
                //startActivityForResult(intent,MAPS_ACTIVITY_REQUEST_CODE);
            }
        });

        Button btnCurrLoc = (Button) findViewById(R.id.btnCurrLoc);
        btnCurrLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"getDeviceLocation: getting the current devices location");

                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

                try{
                    if(mLocationPermissionGranted){

                        Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener(){
                            @Override
                            public void onComplete(@NonNull Task task){
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: found location");
                                    Location currentLocation = (Location) task.getResult();
                                    eqparams.setLocation(currentLocation.getLatitude(),coordParams.longitude);
                                    Toast.makeText(MainActivity.this, "Your current location\nLat: "+currentLocation.getLatitude()+"\nLong: "+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    Log.d(TAG, "onComplete: current location is null. Default coordinated are deployed");
                                    Toast.makeText(MainActivity.this, "Default Coordinates on Athens", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                }catch(SecurityException e){
                    Log.e(TAG,"init, onClick, onComplete: SecurityException: "+e.getMessage());
                }

            }
        });

        Button btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Magn = (EditText) findViewById(R.id.txtMagn);
            Notif = (EditText) findViewById(R.id.txtNot);
            Range = (EditText) findViewById(R.id.txtRange);

            if(Magn.getText() != null || !Magn.getText().equals("")){
                try {
                    magnitude= Double.parseDouble(Magn.getText().toString());
                    eqparams.setMagnitude(magnitude);
                }catch (Exception e){
                    Log.e(TAG,"init, onClick: Exception: " + e.getMessage());
                }

            }
            if(Notif.getText() != null || !Notif.getText().equals("")){
                try {
                    notif= parseInt(Notif.getText().toString());
                    eqparams.setMinNotif(notif);
                }catch (Exception e){
                    Log.e(TAG,"init, onClick: Exception: " + e.getMessage());
                }
            }
            if(Range.getText() != null || !Range.getText().equals("")){
                try {
                    range= parseInt(Range.getText().toString());
                    eqparams.setMaxRange(range);
                }catch (Exception e){
                    Log.e(TAG,"init, onClick: Exception: " + e.getMessage());
                }
            }

            Toast.makeText(MainActivity.this, "Parameters are set!\nEarthquakes notifications to be sent", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this,ConfirmationSettings.class);
            intent.putExtra("earthquake_parameter",eqparams);
            startActivity(intent);

            //sendNotification();

            coordParams=defaultCoordParams;
            range=defMaxRange;
            notif=defMinNotif;
            magnitude=defMinMagnitude;

            }
        });
    }

    private void getLocationPermission(){

        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;

            }else{
                ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Log.d(TAG,"onRequestPermissionsResults: called");
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        mLocationPermissionGranted=false;


        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i=0; i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted=false;
                            Log.d(TAG,"onRequestPermissionsResults: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResults: permission granted");
                    mLocationPermissionGranted= true;

                }
            }
        }
    }



    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and user can make map request
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: /an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You cant make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG,"onActivityResult: pt1");
        Log.d(TAG,"onActivityResult: "+resultCode);
        Log.d(TAG,"onActivityResult: "+requestCode);

        // check that it is the MapsActivity with an OK result
        if (requestCode == MAPS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get double datas from Intent

                double pinLat,pinLng;
                pinLat=data.getDoubleExtra("earthquake_parameters_pin_lat",37.983810);
                pinLng=data.getDoubleExtra("earthquake_parameters_pin_lng",23.727539);

                eqparams.setLocation(pinLat,pinLng);

            }
        }
    }
}