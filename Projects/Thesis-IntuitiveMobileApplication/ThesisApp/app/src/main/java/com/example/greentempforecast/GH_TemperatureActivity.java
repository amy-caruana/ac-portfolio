package com.example.greentempforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class GH_TemperatureActivity extends AppCompatActivity {

    // Declare UI components and variables
    private Button TBacktoMain, TPeriod, TSensor, TSubmit;
    private TextView SensMsg, CurrentReading;
    private static final String TAG = "GH_TemperatureActivity";
    private LineChart lineChart; // Line chart to display sensor data

    Boolean connController = false;
    final String urlstr = "http://192.168.1.244/sensors4.php"; // URL to fetch sensor data
    boolean AsyncRunning = false;
    boolean NetOk = false;
    private int periodSelect = 0;
    private int sensorSelect = 0;
    private ArrayList<String> col1 = new ArrayList<>();
    private ArrayList<Double> col2 = new ArrayList<>();
    private ArrayList<Double> col3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ghtemp); // Set the layout

        // Initialize UI components
        lineChart = findViewById(R.id.lineChart);
        TBacktoMain = findViewById(R.id.BTrendsBack);
        TPeriod = findViewById(R.id.butTPeriod);
        TSensor = findViewById(R.id.butTSensor);
        TSubmit = findViewById(R.id.butTSubmit);
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
            SensMsg.setText("Sensor: " + sensorSelect + " Period: " + periodSelect);
        });

        TSensor.setOnClickListener(v -> {
            // Cycle through sensor options
            sensorSelect = (sensorSelect + 1) % 2;
            switch (sensorSelect) {
                case 0:
                    TSensor.setText("AIR TEMP");
                    break;
                case 1:
                    TSensor.setText("AIR HUMIDITY");
                    break;
            }
            SensMsg.setText("Sensor: " + sensorSelect + " Period: " + periodSelect);
        });

        TSubmit.setOnClickListener(v -> {
            // Submit data fetch request
            SensMsg.setText("SUBMIT-> Sensor: " + sensorSelect + " Period: " + periodSelect);
            col1.clear();
            col2.clear();
            col3.clear();
            if (isOnline()) {
                SensMsg.setText("Is online");
                triggerRequest(urlstr);
            } else {
                SensMsg.setText("Is not online");
            }
        });
    }

    // Method to draw the graph
    public void DrawGraph() {
        int count = col1.size();
        ArrayList<Entry> entries = new ArrayList<>();

        Date date1;
        Date dateStart = null, dateEnd = null;

        if ((sensorSelect == 0) || (sensorSelect == 3)) {
            SensMsg.setText(count + " Last Reading; " + col1.get(count - 1));
            CurrentReading.setText(col3.get(count - 1) + "oC");
            for (int i = 0; i < count; i++) {
                try {
                    date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(col1.get(i));
                    if (i == 0)
                        dateStart = date1;
                    if (i == (count - 1))
                        dateEnd = date1;
                    entries.add(new Entry(date1.getTime(), col3.get(i).floatValue()));
                } catch (Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), " Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        }

        if ((sensorSelect == 1) || (sensorSelect == 2)) {
            SensMsg.setText(count + " Last Reading; " + col1.get(count - 1));
            CurrentReading.setText(col2.get(count - 1) + "%");
            for (int i = 0; i < count; i++) {
                try {
                    date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(col1.get(i));
                    if (i == 0)
                        dateStart = date1;
                    if (i == (count - 1))
                        dateEnd = date1;
                    entries.add(new Entry(date1.getTime(), col2.get(i).floatValue()));
                } catch (Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), " Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        }

        // Setup and customize the LineChart
        LineDataSet dataSet = new LineDataSet(entries, "Sample Data");
        dataSet.setColor(Color.WHITE);
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
            private final DateFormat fmt = new SimpleDateFormat("dd/MM HH:mm", Locale.UK);

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

        // Adjusting Y-axis based on sensor selection
        if (sensorSelect == 0) {
            leftAxis.setAxisMinimum(5f);
            leftAxis.setAxisMaximum(40f);
        }

        if (sensorSelect == 1) {
            leftAxis.setAxisMinimum(40f);
            leftAxis.setAxisMaximum(100f);
        }

        if (sensorSelect == 2) {
            leftAxis.setAxisMinimum(35f);
            leftAxis.setAxisMaximum(50f);
        }

        if (sensorSelect == 3) {
            leftAxis.setAxisMinimum(5f);
            leftAxis.setAxisMaximum(25f);
        }
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
            if (sensorSelect < 2)
                postDataParams.put("sensor", "0");
            else
                postDataParams.put("sensor", "1");
            postDataParams.put("serra", "0");
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
                    Double t = DataElement.getDouble("col2"); // Get air humidity if sensor = 0 / Soil Moisture if sensor = 1
                    col2.add(t);
                    t = DataElement.getDouble("col3"); // Get air temperature readings if sensor = 0 / Soil Temperature if sensor = 1
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
