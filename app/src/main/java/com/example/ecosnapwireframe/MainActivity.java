package com.example.ecosnapwireframe;

import static android.content.ContentValues.TAG;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextView signUp;


    private FirebaseFirestore db;

    private final String authLogTag = "AUTH";

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
                Log.d(authLogTag, "Attempting to authenticate user");
                authenticateUser(valueOf(username.getText()), valueOf(password.getText()));
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

    private void authenticateUser(String uname, String pw) {
        try {
            DocumentReference docRef = db.collection("users").document(uname);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(authLogTag, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> user = document.getData();
                        try {
                            assert user != null;
                            if (user.get("password").equals(pw)) {
                                String usernameValue = username.getText().toString();
                                Log.d(authLogTag, "User is authentic, logging in.");
                                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                intent.putExtra("UserName", usernameValue);
                                // resets password to empty string after Log-In
                                password.setText("");
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Incorrect Username or Password!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.d(authLogTag, "Error authenticating user.");
                        }
                    } else {
                        Log.d(authLogTag, "No such document");
                        Toast.makeText(MainActivity.this, "Incorrect Username or Password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(authLogTag, "get failed with ", task.getException());
                }
            });
        }
        catch(Exception e) {
            Toast.makeText(MainActivity.this, "Incorrect Username or Password!", Toast.LENGTH_SHORT).show();

        }

    }

}