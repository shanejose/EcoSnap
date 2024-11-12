package com.example.ecosnapwireframe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class PhotoResult extends AppCompatActivity {
    private ImageView resultImageView;
    private Button checkResults;
    private Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_result);

        resultImageView = findViewById(R.id.resultImageView);
        checkResults = findViewById(R.id.btnCheckResults);
        logOutBtn = findViewById(R.id.logOut);

        // Retrieve the image URI from the intent
        String imageUri = getIntent().getStringExtra("imageUri");
        File imageFile = new File(imageUri);

        // Display the captured image if it exists
        if (imageFile.exists()) {
            Bitmap originalBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            // Rotate the image if required based on EXIF orientation data
            final Bitmap finalBitmap = rotateImageIfRequired(originalBitmap, imageFile.getAbsolutePath());

            resultImageView.setImageBitmap(finalBitmap);

            // Check image quality immediately after displaying
            if (isLowLight(finalBitmap)) {
                Toast.makeText(PhotoResult.this, "Image quality is poor. Please retake the picture.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Image not found!", Toast.LENGTH_SHORT).show();
        }

        checkResults.setOnClickListener(view -> {
            Intent intent = new Intent(PhotoResult.this, RecycleResultScreen.class);
            intent.putExtra("imageURI", imageUri);
            startActivity(intent);
        });

        logOutBtn.setOnClickListener(view -> {
            Intent intent = new Intent(PhotoResult.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private Bitmap rotateImageIfRequired(Bitmap img, String imagePath) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(imagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    private boolean isLowLight(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        long totalBrightness = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bitmap.getPixel(x, y);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int brightness = (r + g + b) / 3;
                totalBrightness += brightness;
            }
        }

        long averageBrightness = totalBrightness / (width * height);
        return averageBrightness < 50; // Low-light threshold
    }
}
