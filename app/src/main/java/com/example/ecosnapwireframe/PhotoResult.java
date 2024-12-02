package com.example.ecosnapwireframe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import android.content.res.AssetFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecosnapwireframe.ml.EcosnapModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;

public class PhotoResult extends AppCompatActivity {
    private ImageView resultImageView;
    private Button checkResults;
    private Interpreter tfliteInterpreter;
    private TextView resultText;
    String productInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_result);

        resultImageView = findViewById(R.id.resultImageView);
        checkResults = findViewById(R.id.btnCheckResults);
        resultText = findViewById(R.id.result_instruction);


        // Load the TensorFlow Lite model
        try {
            Interpreter.Options options = new Interpreter.Options();
            // Optionally configure options (e.g., enable GPU support, etc.)
            tfliteInterpreter = new Interpreter(loadModelFile(), options);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load model!", Toast.LENGTH_LONG).show();
        }

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
            else{
                String prediction = classifyImage(finalBitmap);
                productInfo = prediction;
                resultText.setText("Item is " + prediction);
                Toast.makeText(PhotoResult.this, "Prediction: " + prediction, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Image not found!", Toast.LENGTH_SHORT).show();
        }

        checkResults.setOnClickListener(view -> {
            Intent intent = new Intent(PhotoResult.this, RecycleResultScreen.class);
            intent.putExtra("imageURI", imageUri);
            if(productInfo != null){
                intent.putExtra("imageResult", productInfo);
            }
            startActivity(intent);
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd("ecosnap_model.tflite");
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error loading model file: " + e.getMessage());
        }
    }



    private String classifyImage(Bitmap bitmap) {

        Log.v("Goes to classify Image","Classify image");
        // Assuming model expects 128x128x3 input
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true); // Resize to 128x128
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * 128 * 128 * 3); // 4 bytes for float
        inputBuffer.order(ByteOrder.nativeOrder());

        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                int pixel = resizedBitmap.getPixel(x, y);
                inputBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f); // R
                inputBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);  // G
                inputBuffer.putFloat((pixel & 0xFF) / 255.0f);         // B
            }
        }

        // Adjust output buffer to match model output shape [1, 1] as a 2D array
        float[][] output = new float[1][1]; // Model output shape [1, 1] as a 2D array

        // Run inference
        tfliteInterpreter.run(inputBuffer, output);

        // Postprocess results
        return output[0][0] > 0.5 ? "Recyclable" : "Non-Recyclable"; // Assuming binary classification (e.g., 0 for non-recyclable, 1 for recyclable)
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
