package com.example.ecosnapwireframe;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;




public class CaptureImageScreen extends AppCompatActivity {

    private Button logOut;
    private Button result;
    private ImageView userPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_capture_image_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        logOut = findViewById(R.id.logOut);
        result = findViewById(R.id.picResults);
        userPicture = findViewById(R.id.capturedImage);

        String photoURI = getIntent().getStringExtra("photoURI");

        if (photoURI !=null){
            Uri cameraPicture = Uri.parse(photoURI);
            userPicture.setImageURI(cameraPicture);
        }

         //Goes to Log-in Screen when Log-Out button is clicked
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaptureImageScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Goes to Recycle Result Screen after clicking the button to check if the product
        //if Recycle or not
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaptureImageScreen.this, RecycleResultScreen.class);
                intent.putExtra("photoURI", photoURI.toString());
                startActivity(intent);
            }
        });


    }
}