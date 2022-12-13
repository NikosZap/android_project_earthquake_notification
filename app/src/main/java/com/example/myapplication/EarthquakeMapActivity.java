package com.example.myapplication;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.databinding.ActivityEarthquakeMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EarthquakeMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private Earthquake eq;
    private GoogleMap mMap;
    private ActivityEarthquakeMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityEarthquakeMapBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



            eq = (Earthquake) getIntent().getSerializableExtra("earthquake");

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng eqcoords = new LatLng(eq.getLattitude(), eq.getLongtitude());
        mMap.addMarker(new MarkerOptions().position(eqcoords).title("Earthquake "+eq.toString()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eqcoords,7f));



    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}