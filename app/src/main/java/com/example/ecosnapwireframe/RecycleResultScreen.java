package com.example.ecosnapwireframe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecycleResultScreen extends AppCompatActivity {
    private ImageButton camera;
    private ImageView userPic;
    private TextView googleMap;
    private TextView picResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recycle_result_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        camera = findViewById(R.id.cameraButton);
        userPic = findViewById(R.id.userPic);
        googleMap = findViewById(R.id.googleMaps);
        picResult = findViewById(R.id.result_instruction);

        String photoUri = getIntent().getStringExtra("imageURI");
        String photoResult = getIntent().getStringExtra("imageResult");

        if(photoUri != null){
            Uri cameraPic = Uri.parse(photoUri);
            userPic.setImageURI(cameraPic);
            userPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if(photoResult != null){
            picResult.setText(photoResult);
        }

        googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecycleResultScreen.this, GoogleMaps.class);
                startActivity(intent);
            }
        });


        if(camera != null){
            // Goes to home screen If camera button is clicked
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RecycleResultScreen.this,
                            HomeScreen.class);
                    startActivity(intent);
                }
            });
        }
        else{
            Log.v("Failed to go Home", "Error when clicked on Camera Image button");
        }
        }

    }