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

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextView signUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.logIn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.userPassword);
        signUp = findViewById(R.id.signUp);

        // Goes to HomeScreen after log-in button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameValue = username.getText().toString();
                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                intent.putExtra("UserName", usernameValue);
                // resets password to empty string after Log-In
                password.setText("");
                startActivity(intent);
            }
        });

        // Goes to SignUp Screen if signUp text is clicked
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpScreen.class);
                // resets password to empty string after Sign-Up
                password.setText("");
                startActivity(intent);
            }
        });



    }
}