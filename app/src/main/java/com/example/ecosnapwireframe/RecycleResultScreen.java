package com.example.ecosnapwireframe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class RecycleResultScreen extends AppCompatActivity {
    private ImageButton camera;
    private ImageView userPic;
    private TextView googleMap;
    private TextView picResult;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocation; // To store the user's current location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recycle_result_screen);

        // Set up edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        camera = findViewById(R.id.cameraButton);
        userPic = findViewById(R.id.userPic);
        googleMap = findViewById(R.id.googleMaps);
        picResult = findViewById(R.id.result_instruction);

        // Initialize location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve image and result data from intent
        String photoUri = getIntent().getStringExtra("imageURI");
        String photoResult = getIntent().getStringExtra("imageResult");

        if (photoUri != null) {
            Uri cameraPic = Uri.parse(photoUri);
            userPic.setImageURI(cameraPic);
            userPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if (photoResult != null) {
            picResult.setText(photoResult);
        }

        // Set up Google Maps link click listener
        googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecyclingCentersInMaps();
            }
        });

        // Set up camera button click listener
        if (camera != null) {
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RecycleResultScreen.this, HomeScreen.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.v("Failed to go Home", "Error when clicked on Camera Image button");
        }

        // Fetch user's current location
        fetchUserLocation();
    }

    // Method to fetch the user's current location
    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not already granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
            return;
        }

        // Get the last known location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.v("Location", "User location fetched: " + userLocation);
                } else {
                    Log.v("Location", "Unable to fetch location. Try again later.");
                    Toast.makeText(RecycleResultScreen.this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("Location Error", "Failed to get location: " + e.getMessage());
            Toast.makeText(RecycleResultScreen.this, "Failed to fetch location. Check your settings.", Toast.LENGTH_SHORT).show();
        });
    }

    // Method to handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the user's location
                fetchUserLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required to find recycling centers.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to open recycling centers in Google Maps
    private void openRecyclingCentersInMaps() {
        if (userLocation != null) {
            String geoUri = String.format("geo:%f,%f?q=recycling centers", userLocation.latitude, userLocation.longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Location not yet available. Please wait.", Toast.LENGTH_SHORT).show();
        }
    }
}
