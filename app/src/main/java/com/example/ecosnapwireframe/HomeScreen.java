package com.example.ecosnapwireframe;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class HomeScreen extends AppCompatActivity {

    private TextView welcomeMsge;
    private Button cameraImg;
    private Button logOutButton;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private Uri photoUri;


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

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        Log.v("image capture", "captured successfully");

                        if(photoUri != null){
                            Intent intent = new Intent(HomeScreen.this, CaptureImageScreen.class);
                            intent.putExtra("photoURI", photoUri.toString());
                            startActivity(intent);
                        }
                        else{
                            Log.v("image capture", "Photo URI is null");
                        }
                    }
                    else{
                        Log.v("image capture", "captured failed");
                    }
                }
        );

        // To capture an image
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(HomeScreen.this,new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
                else{
                    dispatchTakePhotoIntent();
                }
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

    /**
     * Launches the camera app to take a photo and save it to a file
     */
    private void dispatchTakePhotoIntent(){

        try{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }
                catch (IOException e){
                    Log.v("Error creating File", "unable to create the file for the image " + e);
                }

                if(photoFile != null){
                    Log.v("photo","path: " + photoFile.getAbsolutePath());
                    photoUri = FileProvider.getUriForFile(this,
                            "com.example.ecosnapwireframe.fileprovider",
                                photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                    cameraActivityResultLauncher.launch(takePictureIntent);
                    Log.v("Photo file", "not null");
                }
                else{
                    Log.v("Dispatch error", "photo file is null");
                }
            }
        }
        catch (ActivityNotFoundException e){
            Log.v("Cannot open camera", "Camera error");
        }
    }

    /**
     * Request access for camera
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePhotoIntent();
            }
            else {
                Log.v("Permission Error", "Camera permission denied");
                Toast.makeText(this, "Camera permission is required to use camera. " +
                        "Please enable it in settings.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Log.v("Request Code", "req "+ requestCode + ", cam: " + REQUEST_CAMERA_PERMISSION);
            Log.v("Permission Error", "Camera permission denied");
            Toast.makeText(this, "Camera permission is required to use camera. " +
                    "Please enable it in settings.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Creates file for images to be saved in
     */
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "CameraPhoto_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;

        if (storageDir != null && storageDir.exists()){
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            Log.v("File creations", "created successfully");
        }
        else{
            Log.v("File creations", "error creating file");
        }
        return image;
    }
}