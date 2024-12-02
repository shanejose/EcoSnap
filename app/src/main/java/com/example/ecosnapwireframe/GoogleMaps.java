package com.example.ecosnapwireframe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocation; // To store the user's current location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        // Initialize the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            Log.v("GoogleMaps", "MapFragment is not null");
        } else {
            Log.v("GoogleMaps", "MapFragment is null.");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Check for location permission and enable user location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            enableUserLocation();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);

            // Fetch the user's last known location
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mGoogleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    } else {
                        Log.v("Location", "Location is null");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Log.v("Permission Error", "Location permission denied");
                Toast.makeText(this, "Location permission is required to access your location. Please enable it in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to open recycling centers in Google Maps
    private void openRecyclingCentersInMaps() {
        // Ensure userLocation is valid
        if (userLocation != null) {
            // Format the geo URI for Google Maps query
            String geoUri = String.format("geo:%f,%f?q=recycling centers", userLocation.latitude, userLocation.longitude);

            // Create the intent for Google Maps
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            intent.setPackage("com.google.android.apps.maps");

            // Check if Google Maps is installed and start the activity
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Notify user if Google Maps is not installed
                Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Notify user if location is not yet available
            Toast.makeText(this, "Location not yet available. Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    // Example trigger for the method
    public void onFindRecyclingCentersClick() {
        openRecyclingCentersInMaps();
    }
}
