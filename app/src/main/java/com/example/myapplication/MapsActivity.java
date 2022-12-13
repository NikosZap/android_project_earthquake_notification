package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    double setLat,setLng;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();

        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 10f;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng directDevicesLocationCoord;
    private EarthquakesParams eqparams;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        getLocationPermission();


        //String str = (String)getIntent().getSerializableExtra("earthquake_parameter");
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        if(!getIntent().getSerializableExtra("earthquake_parameter").equals(null)) {
            eqparams = (EarthquakesParams) getIntent().getSerializableExtra("earthquake_parameter");
        }


    }

    private void init(){
        Log.d(TAG,"init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {



                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){


                    //execute method for searching
                    geoLocate();
                }

                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: clicked gps");
                getDeviceLocation();

            }
        });
        hideSoftKeyboard();
    }
    private void geoLocate(){
        Log.d(TAG,"geoLocate: geoLocating");

        String searchString = "GR " /*for greek territory */ + mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        Log.d(TAG,"geoLocate: geoLocating pt2");

        try{


            list = geocoder.getFromLocationName(searchString,1);



        }catch(IOException e){
            Log.d(TAG,"geoLocate: geoLocating pt3b");
            Log.e(TAG,"geoLocate: IOException: " + e.getMessage());
        }
        if(list.size()>0){

            Log.d(TAG,"geoLocate: geoLocating pt4");
            Address address = list.get(0);

            Log.d(TAG,"geoLocate: found a location: "+address.toString());
            Toast.makeText(this, "Lat: "+address.getLatitude()+ "\nLong: "+address.getLongitude(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

            if(!eqparams.equals(null)) {

                storeCoordInfo(address.getLatitude(), address.getLongitude());

            }else{
                Toast.makeText(this, "Couldn't save location coordinates!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation: getting the current devices location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener(){
                    @Override
                    public void onComplete(@NonNull Task task){
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");

                        }
                        else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }catch(SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityException: "+e.getMessage());
        }

    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG,"moveCamera: moving the camera to: lat: "+latLng.latitude +", lng: "+ latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);


            mMap.addMarker(options);


        }
        hideSoftKeyboard();
    }
    private void storeCoordInfo(double lat, double lng){
        eqparams.setLocation(lat,lng);
        setLat=lat;
        setLng=lng;
        Toast.makeText(this, "Location parameters added!", Toast.LENGTH_SHORT).show();

    }

    private void initMap(){

        Log.d(TAG,"initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){

        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;
                initMap();
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
                    //initialize map
                    initMap();
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent();
            intent.putExtra("earthquake_parameters_pin_lat",setLat);
            intent.putExtra("earthquake_parameters_pin_lng", setLng);
            setResult(RESULT_OK, intent);
            Log.d(TAG,"onStop: "+RESULT_OK);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
