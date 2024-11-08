package com.example.ecosnapwireframe;


import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;




public class HomeScreen extends AppCompatActivity {

    private TextView welcomeMsge;
    private Button cameraImg;
    private Button logOutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeMsge = findViewById(R.id.welcomeMsge);
        cameraImg = findViewById(R.id.captureImge);
        logOutButton = findViewById(R.id.logOut);

        String username = getIntent().getStringExtra("UserName");


        // If user name is not null include the user name in the Home screen in Welcome message
        if (username != null && !username.isEmpty()){
            String welcomeText = getString(R.string.welcomeUser, username);
            welcomeMsge.setText(welcomeText);

        }


        // To capture an image
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, CaptureImageScreen.class);
                startActivity(intent);
            }
        });


        // Goes to Log-in Screen when Log-Out button is clicked
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }
}