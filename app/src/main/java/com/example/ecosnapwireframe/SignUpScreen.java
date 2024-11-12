package com.example.ecosnapwireframe;

import static android.content.ContentValues.TAG;

import static java.lang.String.valueOf;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    private Button signUp;
    private TextInputEditText username;
    private TextInputEditText password;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        signUp = findViewById(R.id.signUp);
        username = findViewById(R.id.username);
        password = findViewById(R.id.userPassword);

        //Goes to Log-In Screen after creating an account
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!valueOf(username.getText()).equals("") && !valueOf(password.getText()).equals("")) {
                    addUserToFirebase(valueOf(username.getText()), valueOf(password.getText()));
                } else {
                    Toast.makeText(SignUpScreen.this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // Creates a new user if user with username does not already exist
    private void addUserToFirebase(String username, String pw) {

        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "User already exists");
                    Toast.makeText(SignUpScreen.this, "User already exists!", Toast.LENGTH_SHORT).show();

                } else {
                    CollectionReference users = db.collection("users");
                    // Create a new user with a username field
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("password", pw);
                    users.document(username).set(user);
                    Log.v("goes to firesbase","tries to add data");
                    // Add a new document with a generated ID in the "users" collection
                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(documentReference -> {
                                // Successfully added document
                                Log.v("Success adding a user","added a user");
                                Intent intent = new Intent(SignUpScreen.this, MainActivity.class);
                                // resets password to empty string after Sign-Up
                                password.setText("");
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                // Error adding document
                                Log.v("Error adding a user",e.getMessage());
                            });
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

}