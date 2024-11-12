package com.example.ecosnapwireframe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

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

        // Check login state on app start
        checkLoginState();

        // Log in button click event
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = username.getText() != null ? username.getText().toString().trim() : "";
                String pw = password.getText() != null ? password.getText().toString().trim() : "";

                if (uname.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username and Password cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(uname, pw);
                }
            }
        });

        // Sign-up text click event
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpScreen.class);
                password.setText("");  // Reset password field for Sign-Up
                startActivity(intent);
            }
        });
    }

    private void checkLoginState() {
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String username = preferences.getString("username", null);

        if (isLoggedIn && username != null) {
            Intent intent = new Intent(this, HomeScreen.class);
            intent.putExtra("UserName", username);
            startActivity(intent);
            finish();  // Prevents going back to login screen
        }
    }

    private void authenticateUser(String uname, String pw) {
        DocumentReference docRef = db.collection("users").document(uname);
        docRef.get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> user = document.getData();
                    if (user != null && pw.equals(user.get("password"))) {
                        saveLoginState(uname);
                        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                        intent.putExtra("UserName", uname);
                        password.setText("");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect Username or Password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Username or Password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(authLogTag, "get failed with ", task.getException());
                Toast.makeText(MainActivity.this, "Login Failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginState(String username) {
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.apply();
    }
}
