package com.example.greentempforecast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class WeatherStationActivity extends AppCompatActivity {

    // Declare UI components and variables
    private Button TBacktoMain, TPeriod, TSensor, TSubmit, TTrend, TRecommendations;
    private TextView SensMsg, CurrentReading;
    private static final String TAG = "WeatherStationActivity";
    private LineChart lineChart; // Line chart to display sensor data

    Boolean connController = false;
    final String urlstr = "http://192.168.1.244/weather.php"; // URL to fetch sensor data
    boolean AsyncRunning = false;
    boolean NetOk = false;
    private int periodSelect = 0;
    private int sensorSelect = 0; // Only one sensor type (TEMP)
    private int trendSelect = 0;
    private ArrayList<String> col1 = new ArrayList<>();
    private ArrayList<Double> col3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_weather); // Set the layout

        // Initialize UI components
        lineChart = findViewById(R.id.lineChart);
        TBacktoMain = findViewById(R.id.BTrendsBack);
        TPeriod = findViewById(R.id.butTPeriod);
        TSensor = findViewById(R.id.butTSensor);
        TTrend = findViewById(R.id.butTTrend);
        TSubmit = findViewById(R.id.butTSubmit);
        TRecommendations = findViewById(R.id.butTRecommendations);
        SensMsg = findViewById(R.id.SensArray);
        CurrentReading = findViewById(R.id.curr_reading);

        // Set click listeners for buttons
        TBacktoMain.setOnClickListener(view -> finish());

        TPeriod.setOnClickListener(v -> {
            // Cycle through period options
            periodSelect = (periodSelect + 1) % 4;
            switch (periodSelect) {
                case 0:
                    TPeriod.setText("2 Days");
                    break;
                case 1:
                    TPeriod.setText("1 Week");
                    break;
                case 2:
                    TPeriod.setText("2 Weeks");
                    break;
                case 3:
                    TPeriod.setText("1 Month");
                    break;
            }
            SensMsg.setText("Period: " + periodSelect);
        });

        TSensor.setOnClickListener(v -> {
            // Since only temperature is available, just update the text
            TSensor.setText("TEMP");
            SensMsg.setText("Sensor: TEMP");
        });

        TTrend.setOnClickListener(v -> {
            // Cycle through trend options
            trendSelect = (trendSelect + 1) % 2;
            switch (trendSelect) {
                case 0:
                    TTrend.setText("HISTORICAL");
                    break;
                case 1:
                    TTrend.setText("PREDICTION");
                    break;
            }
            SensMsg.setText("Period: " + periodSelect + " Trend: " + trendSelect);
        });

        TSubmit.setOnClickListener(v -> {
            // Submit data fetch request
            SensMsg.setText("SUBMIT-> Period: " + periodSelect + " Trend: " + trendSelect);
            col1.clear();
            col3.clear();
            if (isOnline()) {
                SensMsg.setText("Is online");
                triggerRequest(urlstr);
            } else {
                SensMsg.setText("Is not online");
            }
        });

        TRecommendations.setOnClickListener(v -> {
            // Show recommendations based on the maximum predicted temperature
            double maxTemp = getMaxPredictedTemperature();
            showRecommendations(maxTemp);
        });
    }

    // Method to draw the graph
    public void DrawGraph() {
        int count = col1.size();
        ArrayList<Entry> entries = new ArrayList<>();

        Date date1;
        Date dateStart = null, dateEnd = null;

        SensMsg.setText(count + " Last Reading; " + col1.get(count - 1));
        CurrentReading.setText(col3.get(count - 1) + "oC");
        for (int i = 0; i < count; i++) {
            try {
                if (trendSelect == 0) { // Historical
                    date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(col1.get(i));
                } else { // Prediction
                    date1 = new SimpleDateFormat("H", Locale.US).parse(col1.get(i)); // Assuming col1 for prediction is just hour
                }
                if (i == 0)
                    dateStart = date1;
                if (i == (count - 1))
                    dateEnd = date1;
                entries.add(new Entry(date1.getTime(), col3.get(i).floatValue()));
            } catch (Exception e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }

        // Setup and customize the LineChart
        LineDataSet dataSet = new LineDataSet(entries, "Temperature Data");
        dataSet.setColor(Color.WHITE);
        dataSet.setLineWidth(2f); // Adjust line width if needed
        dataSet.setDrawCircles(false); // Optionally hide data points
        dataSet.setDrawValues(false); // Optionally hide data values
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();

        // Customizing the X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final DateFormat fmt = trendSelect == 0 ?
                    new SimpleDateFormat("dd/MM HH:mm", Locale.UK) : new SimpleDateFormat("H", Locale.US);

            @Override
            public String getFormattedValue(float value) {
                return fmt.format(new Date((long) value));
            }
        });

        // Customizing the Y axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable right Y-Axis

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        // Adjusting Y-axis based on temperature range
        leftAxis.setAxisMinimum(5f);
        leftAxis.setAxisMaximum(40f);
    }

    // Method to get the maximum predicted temperature from the first 24 samples
    private double getMaxPredictedTemperature() {
        double maxTemp = Double.MIN_VALUE;
        int sampleCount = Math.min(col3.size(), 24); // Ensure we only consider the first 24 samples

        for (int i = 0; i < sampleCount; i++) {
            double temp = col3.get(i);
            if (temp > maxTemp) {
                maxTemp = temp;
            }
        }
        return maxTemp;
    }

    // Method to show recommendations based on the maximum predicted temperature
    private void showRecommendations(double maxTemp) {
        String recommendation;

        if (maxTemp >= 30) {
            recommendation = "It is predicted that temperature is going to rise to 30 degrees or higher, so it is suggested to open the louvers at the 75% setting.";
        } else if (maxTemp >= 22 && maxTemp < 30) {
            recommendation = "It is predicted that temperature is going to be between 22 - 28 degrees, so it is suggested to open the louvers at the 50% setting.";
        } else if (maxTemp >= 20 && maxTemp < 22) {
            recommendation = "It is predicted that temperature is going to be between 20 - 21 degrees, so it is suggested to open the louvers at the 25% setting.";
        } else {
            recommendation = "It is predicted that temperature is going to be 20 degrees or lower, so it is suggested to fully close the louvers.";
        }

        // Show recommendation in a popup
        new AlertDialog.Builder(this)
                .setTitle("Recommendations")
                .setMessage(recommendation)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    // Check network connectivity
    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        boolean isAvailable = false;
        NetOk = false;

        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true;
                NetOk = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true;
                NetOk = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true;
                NetOk = true;
            }
        }
        return isAvailable;
    }

    // Trigger network request in a separate thread
    public void triggerRequest(String urlstring) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> sendRequest(urlstring));
    }

    // Send network request to fetch sensor data
    public void sendRequest(String urlstring) {
        try {
            AsyncRunning = true;
            connController = false;
            URL url = new URL(urlstring);

            // Create JSON object for post data
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("period", periodSelect);
            postDataParams.put("sensor", sensorSelect);
            postDataParams.put("trend", trendSelect);
            Log.e("params", postDataParams.toString());

            System.setProperty("http.keepAlive", "false");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("connection", "close");
            conn.setChunkedStreamingMode(0);
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Send post data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            // Handle response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                conn.disconnect();
                UpdateUi(sb.toString());
            } else {
                UpdateUi("false : " + responseCode);
            }
        } catch (Exception e) {
            UpdateUi("Exception: " + e.getMessage());
        }
    }

    // Update UI with fetched data
    public void UpdateUi(String result) {
        if ((result != null) && (!result.startsWith("false")) && (!result.startsWith("Exception"))) {
            JSONObject jsonResponse;
            try {
                jsonResponse = new JSONObject(result);
                JSONArray SensorData = jsonResponse.getJSONArray("SensorData");
                for (int i = 0; i < SensorData.length(); i++) {
                    JSONObject DataElement = SensorData.getJSONObject(i);
                    String s = DataElement.getString("col1"); // Get timestamp of sample
                    col1.add(s);
                    Double t = DataElement.getDouble("col2"); // Get air temperature readings if sensor = 0
                    col3.add(t);
                }
                if (col3.size() > 5)
                    runOnUiThread(this::DrawGraph);
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        } else {
            if (result == null) {
                Log.e(TAG, "Couldn't get json from server. Result = null");
            }
            if (result.startsWith("false")) {
                Log.e(TAG, "Couldn't get json from server. Result = false");
            }
            if (result.startsWith("Exception")) {
                Log.e(TAG, "Couldn't get json from server. Result = Exception");
            }
        }
        AsyncRunning = false;
    }

    // Convert JSON object to URL encoded string
    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}
