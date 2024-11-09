package com.example.ecosnapwireframe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextView signUp;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

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

        db = FirebaseFirestore.getInstance();

        // Goes to HomeScreen after log-in button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameValue = username.getText().toString();
                addUserToFirebase(usernameValue);
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


    // Firebase test Function
    // Adds user names to database "user"
    private void addUserToFirebase(String username){

        // Create a new user with a username field
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        Log.v("goes to firesbase","tries to add data");
        // Add a new document with a generated ID in the "users" collection
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    // Successfully added document
                   Log.v("Success adding a user","added a user");
                })
                .addOnFailureListener(e -> {
                    // Error adding document
                    Log.v("Error adding a user",e.getMessage());
                });
    }

}