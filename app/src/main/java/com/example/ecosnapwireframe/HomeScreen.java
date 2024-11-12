package com.example.ecosnapwireframe;

import android.content.Intent;
import android.content.SharedPreferences;
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

        if (username != null && !username.isEmpty()) {
            String welcomeText = getString(R.string.welcomeUser, username);
            welcomeMsge.setText(welcomeText);
        }

        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, CaptureImageScreen.class);
                startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearLoginState();
                Intent intent = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(intent);
                finish();  // Prevent user from going back to HomeScreen after logout
            }
        });
    }

    private void clearLoginState() {
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clear all saved data
        editor.apply();
    }
}
