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

    private Button logOut;
    private ImageButton camera;
    private ImageView userPic;
    private TextView googleMap;

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

        logOut = findViewById(R.id.logOut);
        camera = findViewById(R.id.cameraButton);
        userPic = findViewById(R.id.userPic);
        googleMap = findViewById(R.id.googleMaps);

        String photoUri = getIntent().getStringExtra("photoURI");

        if(photoUri != null){
            Uri cameraPic = Uri.parse(photoUri);
            userPic.setImageURI(cameraPic);
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

        if (logOut != null){
            // Goes to Log-in Screen when Log-Out button is clicked
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RecycleResultScreen.this,
                            MainActivity.class);
                    startActivity(intent);
                }
            });
        }
        else{
            Log.v("Failed to go to Log-in screen",
                    "Error when clicked on Result screen of Login");
        }

    }
}