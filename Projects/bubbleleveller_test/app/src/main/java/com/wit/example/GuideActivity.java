package com.wit.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wit.example.classic.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GuideActivity extends AppCompatActivity {
    private TextView guideText;
    private TextView jackControllerText;
    private int currentStep = -1;

    // Create URL for the PHP script (Replace with your server's URL)
    String urlString = "http://192.168.4.1/conduit.php";  // Replace with your actual PHP page URL
    private String[] steps = {
            "Step 1: Place the sensor on the equipment",
            "Step 2: Connect to the sensor",
            "Step 3: Place the jack in the middle of the front chassis of the equipment",
            "Step 4: Raise the equipment using the jack controller",
            "Step 5: Go to the bubble leveller",
            "Step 6: Manually lower the feet of the equipment",
            "Step 7: Adjust the feet until the bubble is in the middle of the circle in the X direction",
            "Step 8: Lower the jack and place it behind the equipment",
            "Step 9: Repeat the steps but the bubble needs to be level in both X and Y directions",
    };

    private Button btnUp, btnDown, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(v -> {
            Intent intent = new Intent(GuideActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Button bubbleLevel2Button = findViewById(R.id.bubbleLevelButton);
        bubbleLevel2Button.setOnClickListener(v -> {
            Intent intent = new Intent(GuideActivity.this, BubbleActivity.class);
            startActivity(intent);
        });

        guideText = findViewById(R.id.guideText);
        jackControllerText = findViewById(R.id.jackControllerText);  // TextView for jack controller
        Button nextButton = findViewById(R.id.nextButton);
        Button previousButton = findViewById(R.id.previousButton);

        // Initialize buttons for Up, Down, and Stop
        btnUp = findViewById(R.id.upButton);
        btnDown = findViewById(R.id.downButton);
        btnStop = findViewById(R.id.stopButton);

        // Initially hide Up, Down, and Stop buttons
        toggleControlButtonsVisibility(View.GONE);

        nextButton.setOnClickListener(v -> {
            if (currentStep < steps.length - 1) {
                currentStep++;
                guideText.setText(steps[currentStep]);
                handleStepChange();
            }
        });

        previousButton.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                guideText.setText(steps[currentStep]);
                handleStepChange();
            }
        });

        // Set onClick listeners for control buttons
        btnUp.setOnClickListener(v -> sendCommandToDatabase(-1));  // -1 for up
        btnDown.setOnClickListener(v -> sendCommandToDatabase(-2));  // -2 for down
        btnStop.setOnClickListener(v -> sendCommandToDatabase(0));  // 0 for stop
    }

    // Method to toggle visibility of Up, Down, and Stop buttons
    private void toggleControlButtonsVisibility(int visibility) {
        btnUp.setVisibility(visibility);
        btnDown.setVisibility(visibility);
        btnStop.setVisibility(visibility);
        jackControllerText.setVisibility(visibility);
    }

    // Method to handle visibility logic based on the current step
    private void handleStepChange() {
        if (currentStep >= 3) {  // Step 4 or later (zero-indexed)
            toggleControlButtonsVisibility(View.VISIBLE);
        } else {
            toggleControlButtonsVisibility(View.GONE);
        }
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
}
