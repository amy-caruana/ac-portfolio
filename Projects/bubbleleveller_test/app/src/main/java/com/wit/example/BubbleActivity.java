package com.wit.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wit.example.classic.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BubbleActivity extends AppCompatActivity {

    private float angleX = 0f; // Angle from Bluetooth sensor
    private float angleY = 0f;

    // Create URL for the PHP script (Replace with your server's URL)
    String urlString = "http://192.168.4.1/conduit.php";  // Replace with your actual PHP page URL
    private BubbleView bubbleView;
    private BroadcastReceiver angleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        Button guideButton2 = findViewById(R.id.guideButton2);
        guideButton2.setOnClickListener(v -> {
            Intent intent = new Intent(BubbleActivity.this, GuideActivity.class);
            startActivity(intent);
        });

        // Initialize buttons for Up, Down, and Stop
        Button btnUp = findViewById(R.id.upButton2);
        Button btnDown = findViewById(R.id.downButton2);
        Button btnStop = findViewById(R.id.stopButton2);

        // Set onClick listeners for buttons
        btnUp.setOnClickListener(v -> sendCommandToDatabase(-1));  // -1 for up
        btnDown.setOnClickListener(v -> sendCommandToDatabase(-2));  // -2 for down
        btnStop.setOnClickListener(v -> sendCommandToDatabase(0));  // 0 for stop

        // Back button initialization
        Button backButton = findViewById(R.id.backButton2);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            Log.e("BubbleActivity", "Back Button is null!");
        }

        // Initialize custom BubbleView
        bubbleView = new BubbleView();
        ConstraintLayout layout = findViewById(R.id.main);
        layout.addView(bubbleView);

        // Restore saved angles (optional)
        restoreAngles();

        // Register the BroadcastReceiver to receive angle updates
        angleReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.wit.example.UPDATE_BUBBLE".equals(intent.getAction())) {
                    angleX = intent.getFloatExtra("ANGLE_X", 0f);
                    angleY = intent.getFloatExtra("ANGLE_Y", 0f) +90f;
                    bubbleView.updateBubblePosition(angleX, angleY);
                }
            }
        };

        // Register the BroadcastReceiver with the appropriate intent filter
        IntentFilter filter = new IntentFilter("com.wit.example.UPDATE_BUBBLE");
        registerReceiver(angleReceiver, filter);
    }

    // Method to send the command to the PHP script
    private void sendCommandToDatabase(int command) {
        new Thread(() -> {
            try {
                Log.d("NetworkDebug", "Starting network request...");

                Log.d("NetworkDebug", "URL: " + urlString);

                // Create the HTTP connection
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Create the JSON data to send
                JSONObject jsonData = new JSONObject();
                jsonData.put("command", command);  // Add the command value to the JSON object
                Log.d("NetworkDebug", "Sending JSON: " + jsonData.toString());

                // Send the JSON data to the PHP script
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                    Log.d("NetworkDebug", "JSON Sent");
                }

                // Get the response from the PHP script
                int responseCode = urlConnection.getResponseCode();
                Log.d("NetworkDebug", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the server
                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(in);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Log the response for debugging
                    Log.d("NetworkDebug", "Server Response: " + response.toString());

                    // Handle the response (for example, show a success message)
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Command sent: " + command, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Log the error response code
                    Log.d("NetworkDebug", "Error code: " + responseCode);

                    // Handle server response error
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Error sending command", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("NetworkError", "Error sending command: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Failed to send command to server", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAngles(); // Save angle data when the activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreAngles(); // Restore angle data when the activity resumes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(angleReceiver);
    }

    private void saveAngles() {
        SharedPreferences prefs = getSharedPreferences("BubbleData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("ANGLE_X", angleX);
        editor.putFloat("ANGLE_Y", angleY);
        editor.apply();
    }

    private void restoreAngles() {
        SharedPreferences prefs = getSharedPreferences("BubbleData", MODE_PRIVATE);
        angleX = prefs.getFloat("ANGLE_X", 0f);
        angleY = prefs.getFloat("ANGLE_Y", 0f);
    }

    private class BubbleView extends View {
        private Paint circlePaint;
        private Paint bubblePaint;
        private Paint textPaint;

        private float bubbleX, bubbleY;

        public BubbleView() {
            super(BubbleActivity.this);

            // Paint for the background circle
            circlePaint = new Paint();
            circlePaint.setAntiAlias(true);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeWidth(8);
            circlePaint.setColor(Color.WHITE);

            // Paint for the bubble
            bubblePaint = new Paint();
            bubblePaint.setAntiAlias(true);
            bubblePaint.setStyle(Paint.Style.FILL);
            bubblePaint.setColor(Color.RED);

            // Paint for the text
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(50f);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Calculate the center of the view
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;
            float radius = 300; // Circle radius
            float smallCircleRadius = 50; // Small circle radius

            // Check if bubble is centered
            boolean isCentered = Math.abs(angleX) < 1f && Math.abs(angleY) < 1f;

            // Update bubble color
            bubblePaint.setColor(isCentered ? Color.GREEN : Color.RED);

            // Draw the reference circle
            canvas.drawCircle(centerX, centerY, radius, circlePaint);

            // Draw the bubble
            canvas.drawCircle(bubbleX, bubbleY, 50, bubblePaint); // Bubble size = 50dp

            // Draw the cross in the circle
            Paint crossPaint = new Paint();
            crossPaint.setAntiAlias(true);
            crossPaint.setColor(Color.WHITE);
            crossPaint.setStrokeWidth(5);

            // Horizontal line
            canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, crossPaint);
            // Vertical line
            canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, crossPaint);

            // Draw the small circle at the center
            Paint smallCirclePaint = new Paint();
            smallCirclePaint.setAntiAlias(true);
            smallCirclePaint.setStyle(Paint.Style.STROKE);
            smallCirclePaint.setStrokeWidth(5);
            smallCirclePaint.setColor(Color.WHITE);

            canvas.drawCircle(centerX, centerY, smallCircleRadius, smallCirclePaint);

            // Draw the angle values
            canvas.drawText("Angle X: " + angleX, 50, 100, textPaint);
            canvas.drawText("Angle Y: " + angleY, 50, 150, textPaint);
        }

        public void updateBubblePosition(float angleX, float angleY) {
            // Translate angles into bubble coordinates
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;
            float sensitivity = 10f; // Adjust this for more/less movement

            bubbleX = centerX + (angleX * sensitivity);
            bubbleY = centerY + (angleY * sensitivity);

            // Prevent bubble from going outside the circle
            float radius = 300; // Circle radius
            float dx = bubbleX - centerX;
            float dy = bubbleY - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > radius) {
                float scale = radius / distance;
                bubbleX = centerX + dx * scale;
                bubbleY = centerY + dy * scale;
            }

            invalidate(); // Redraw the view
        }
    }
}
