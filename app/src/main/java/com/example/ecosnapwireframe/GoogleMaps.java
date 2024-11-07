package com.example.ecosnapwireframe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.security.Permission;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {

    private  GoogleMap mGoogleMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_google_maps);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
            Log.v("Google Maps", "MapFragment is not null");

        }
        else{
            Log.v("GoogleMaps", "MapFragment is null.");
        }

//        mapView = findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(ContextCompat.checkSelfPermission(GoogleMaps.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GoogleMaps.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
        else{
            enableUserLocation();
        }

    }

    private void enableUserLocation(){
        if(ActivityCompat.checkSelfPermission(GoogleMaps.this,Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            mGoogleMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mGoogleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                    }
                    else{
                        Log.v("Location", "Location is null");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enableUserLocation();
            }
            else {
                Log.v("Permission Error", "Location permission denied");
                Toast.makeText(this, "Location permission is required to access your location. " +
                        "Please enable it in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }
    /*

    @Override
    protected void onResume() {
        super.onResume();
        if (googleMap != null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Log.v("Map on Resume", "goes here");
        }
        else{
            Log.v("Map on Resume", "google map is null");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleMap != null) {
            // Add any actions needed when the map is started
            Log.v("Map on Start", "google map is not null");
        }
        else{
            Log.v("Map on Start", "google map is null");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Add any actions needed when the map is stopped
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
    }*/
}