package com.example.greentempforecast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class PumpsFansActivity extends AppCompatActivity {

    boolean connMode = false;
    boolean ErrorState = false;     //0 = no error, 1 = error condition
    int ActionMode = 0;
    String Message = "Not initialised";

    private int fans_state = -2;
    private int pump_state = -2;
    private int on_minutes = 10;
    private int off_minutes = 20;
    private int on_time_hours = 6;
    private int on_time_mins = 0;
    private int off_time_hours = 18;
    private int off_time_mins = 0;

    private int url_select = 0;

    boolean NetOk = false;
    boolean DeviceActive = false;
    boolean AsyncRunning = false;
    private static final String TAG = "NewPumpFans";
    private TextView FansStatusBar, activitydebug, jsondebug;
    private TextView mins_on, mins_off, time_on, time_off;
    private Button FansButton, PumpButton;
    private Button GoBackButton, SubmitFansSettingsButton;
    private Button t_on_plus_button, t_on_minus_button, t_off_plus_button, t_off_minus_button;
    private Button t_d_on_plus_button, t_d_on_minus_button, t_d_off_plus_button, t_d_off_minus_button;
    private RadioButton fanOnOff;
    private String urlstr = "http://10.6.1.3/new_pump_fans.php";
    private String urlstr2 = "http://10.6.1.3/new_fans.php";
    private String msgExe = "";
    private int actTimer = 0;
    private int idleCount = 0;
    private int jsCounter = 0;
    private int expirationSec = 0;
    private int someRequestNumber = 0;
    private int FanState = 0;
    private String actionCommand = "0";
    TimerTask mTimerTask;
    TimerTask mTimerIdleTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    Timer s = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_pumps_fans);

        FansButton = findViewById(R.id.fans_button);
        PumpButton = findViewById(R.id.pump_button);
        SubmitFansSettingsButton = findViewById(R.id.submit_fan_settings);
        GoBackButton = findViewById(R.id.fans_pump_exit);
        FansStatusBar = (TextView)findViewById(R.id.textViewPumpFansStatus);
        activitydebug = (TextView)findViewById(R.id.textViewDebug);
        jsondebug = (TextView)findViewById(R.id.textViewJson);
        t_on_plus_button = findViewById(R.id.fans_on_plus);
        t_on_minus_button = findViewById(R.id.fans_on_minus);
        t_off_plus_button = findViewById(R.id.fans_off_plus);
        t_off_minus_button = findViewById(R.id.fans_off_minus);
        t_d_on_plus_button = findViewById(R.id.fans_time_on_plus);
        t_d_on_minus_button = findViewById(R.id.fans_time_on_minus);
        t_d_off_plus_button = findViewById(R.id.fans_time_off_plus);
        t_d_off_minus_button = findViewById(R.id.fans_time_off_minus);
        mins_on = findViewById(R.id.ftime_on);
        mins_off = findViewById(R.id.ftime_off);
        time_on = findViewById(R.id.dtime_on);
        time_off = findViewById(R.id.dtime_off);
        fanOnOff = findViewById(R.id.fanradioButton);
        fanOnOff.setEnabled(true);

        updateDisplay();

        t_on_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (on_minutes != -1)) {
                    connMode = false;
                    actTimer = 0;
                    on_minutes = on_minutes + 1;
                    if (on_minutes > 95)
                        on_minutes = 95;
                    mins_on.setText(Integer.toString(on_minutes));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_on_minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (on_minutes != -1)) {
                    connMode = false;
                    actTimer = 0;
                    on_minutes = on_minutes - 1;
                    if (on_minutes < 1)
                        on_minutes = 1;
                    mins_on.setText(Integer.toString(on_minutes));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_off_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (off_minutes != -1)) {
                    connMode = false;
                    actTimer = 0;
                    off_minutes = off_minutes + 1;
                    if (off_minutes > 95)
                        off_minutes = 95;
                    mins_off.setText(Integer.toString(off_minutes));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_off_minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (off_minutes != -1)) {
                    connMode = false;
                    actTimer = 0;
                    off_minutes = off_minutes - 1;
                    if (off_minutes < 0)
                        off_minutes = 0;
                    mins_off.setText(Integer.toString(off_minutes));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_d_on_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (on_time_mins != -1) && (on_time_hours != -1)) {
                    connMode = false;
                    actTimer = 0;
                    on_time_mins = on_time_mins + 5;
                    if (on_time_mins == 60) {
                        on_time_mins = 0;
                        on_time_hours = on_time_hours + 1;
                        if (on_time_hours == 24)
                            on_time_hours = 0;
                    }
                    if (on_time_mins >= 10)
                        time_on.setText(Integer.toString(on_time_hours) + ":" + Integer.toString(on_time_mins));
                    else
                        time_on.setText(Integer.toString(on_time_hours) + ":0" + Integer.toString(on_time_mins));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_d_on_minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (on_time_mins != -1) && (on_time_hours != -1)) {
                    connMode = false;
                    actTimer = 0;
                    on_time_mins = on_time_mins - 5;
                    if (on_time_mins == -5) {
                        on_time_mins = 55;
                        on_time_hours = on_time_hours - 1;
                        if (on_time_hours == -1)
                            on_time_hours = 23;
                    }
                    if (on_time_mins >= 10)
                        time_on.setText(Integer.toString(on_time_hours) + ":" + Integer.toString(on_time_mins));
                    else
                        time_on.setText(Integer.toString(on_time_hours) + ":0" + Integer.toString(on_time_mins));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_d_off_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (off_time_mins != -1) && (off_time_hours != -1)) {
                    connMode = false;
                    actTimer = 0;
                    off_time_mins = off_time_mins + 5;
                    if (off_time_mins == 60) {
                        off_time_mins = 0;
                        off_time_hours = off_time_hours + 1;
                        if (off_time_hours == 24)
                            off_time_hours = 0;
                    }
                    if (off_time_mins >= 10)
                        time_off.setText(Integer.toString(off_time_hours) + ":" + Integer.toString(off_time_mins));
                    else
                        time_off.setText(Integer.toString(off_time_hours) + ":0" + Integer.toString(off_time_mins));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        t_d_off_minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fans_state == 20) && (off_time_mins != -1) && (off_time_hours != -1)) {
                    connMode = false;
                    actTimer = 0;
                    off_time_mins = off_time_mins - 5;
                    if (off_time_mins == -5) {
                        off_time_mins = 55;
                        off_time_hours = off_time_hours - 1;
                        if (off_time_hours == -1)
                            off_time_hours = 23;
                    }
                    if (off_time_mins >= 10)
                        time_off.setText(Integer.toString(off_time_hours) + ":" + Integer.toString(off_time_mins));
                    else
                        time_off.setText(Integer.toString(off_time_hours) + ":0" + Integer.toString(off_time_mins));
                } else {
                    new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("FANS RUNNING").setMessage("Press any key to exit")
                            .setPositiveButton("Exit", null).show();
                }
            }
        });

        SubmitFansSettingsButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (fans_state == 20) {    //update fan timer settings only when fans are off
                   url_select = 1;
                   new PumpsFansActivity.SendRequest().execute("1", urlstr2);
               }
           }
       });

        GoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url_select = 0;
                if (fans_state == 20)     //Switch on the fans
                    new PumpsFansActivity.SendRequest().execute("21", urlstr);
                if (fans_state == 21)     //Switch off the fans
                    new PumpsFansActivity.SendRequest().execute("20", urlstr);
            }
        });

        PumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(PumpsFansActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("TURN ON PUMP").setMessage("Are you sure you want to switch on Pump?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            url_select = 0;
                            if (pump_state == 10)     //Switch on the pump
                                new PumpsFansActivity.SendRequest().execute("11", urlstr);
                            if (pump_state == 11)     //Switch off the pump
                                new PumpsFansActivity.SendRequest().execute("10", urlstr);
                            Toast.makeText(PumpsFansActivity.this, "Pump Started",Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", null).show();
            }
        });

        if (isOnline(2) && (!connMode) && (!AsyncRunning)) {
            FansStatusBar.setText("Establishing contact");
            connMode = true;
            actTimer = 0;
            jsCounter = 0;
            idleCount = 0;
            url_select = 1;
            new PumpsFansActivity.SendRequest().execute("0", urlstr2);
            //new NewPumpFans.SendRequest().execute("0", urlstr);
            updateDisplay();
            stopIdleTask();
            doTimerTask();
        } else {
            FansStatusBar.setText("System is not online");
        }

    }

    public void updateDisplay() {

        FansStatusBar.setText("JSON data Pump: " + pump_state + " Fans: " + fans_state);
        if (fans_state == -1) {
            FansButton.setTextColor(Color.BLACK);
            FansButton.setBackgroundColor(Color.GRAY);
            FansButton.setText("OFFLINE");
        }
        if (pump_state == -1) {
            PumpButton.setTextColor(Color.BLACK);
            PumpButton.setBackgroundColor(Color.GRAY);
            PumpButton.setText("OFFLINE");
        }
        if (fans_state == 20) {
            FansButton.setTextColor(Color.WHITE);
            FansButton.setBackgroundColor(Color.RED);
            FansButton.setText("ON");
        }
        if (pump_state == 10) {
            PumpButton.setTextColor(Color.WHITE);
            PumpButton.setBackgroundColor(Color.RED);
            PumpButton.setText("ON");
        }
        if (fans_state == 21) {
            FansButton.setTextColor(Color.BLACK);
            FansButton.setBackgroundColor(Color.GREEN);
            FansButton.setText("OFF");
        }
        if (pump_state == 11) {
            PumpButton.setTextColor(Color.BLACK);
            PumpButton.setBackgroundColor(Color.GREEN);
            PumpButton.setText("OFF");;
        }
        if (FanState == 0)
            fanOnOff.setChecked(false);
        else
            fanOnOff.setChecked(true);
    }

    public void doTimerTask(){
        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            actTimer = actTimer + 2;
                            jsondebug.setText(Integer.toString(actTimer));
                            if ((actTimer > 60) && connMode && !AsyncRunning) {           //two minutes have passed
                                actTimer = 0;
                                connMode = false;
                                stopTask();
                                idleCount = 0;
                                doIdleTask();
                                finish();
                            }
                            if (NetOk && connMode && (jsCounter == 0)) {
                                url_select = 0;
                                new PumpsFansActivity.SendRequest().execute(actionCommand, urlstr);
                                updateDisplay();
                            }
                            if (!NetOk || (jsCounter > 0)) {
                                actTimer = 0;
                                connMode = false;
                                stopTask();
                                idleCount = 0;
                                doIdleTask();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("TIMER", "TimerTask run");
                    }
                });
            }};
        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTask, 500, 2000);  //
    }

    public void stopTask(){
        if(mTimerTask!=null){
            Log.d("TIMER", "timer canceled");
            mTimerTask.cancel();
        }
    }

    public void doIdleTask(){
        mTimerIdleTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            idleCount++;
                            if (idleCount > 1440)
                                idleCount = 0;
                            if (isOnline(4) && !AsyncRunning) {
                                url_select = 0;
                                new PumpsFansActivity.SendRequest().execute(actionCommand, urlstr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("IDLE TIMER", "TimerTask run");
                    }
                });
            }};
        // public void schedule (TimerTask task, long delay, long period)
        s.schedule(mTimerIdleTask, 500, 30000);  //
    }

    public void stopIdleTask(){
        if(mTimerIdleTask!=null){
            Log.d("IDLE TIMER", "timer canceled");
            mTimerIdleTask.cancel();
        }
    }

    public boolean isOnline(int fg) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            NetOk = true;
            return true;
        } else {
            NetOk = false;
            return false;
        }
    }

    public class SendRequest extends AsyncTask<String, Void, String> {
        private String actions, urlstring;
        protected void onPreExecute(){}
        protected String doInBackground(String... arg0) {
            try{
                AsyncRunning = true;
                actions = arg0[0];
                urlstring = arg0[1];
                URL url = new URL(urlstring);

                FansStatusBar.setText("Sending : " + actions);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("action", actions);
                if ((url_select == 1) && (actions == "1")) {
                    postDataParams.put("time_on", String.valueOf(on_minutes));
                    postDataParams.put("time_off", String.valueOf(off_minutes));
                    postDataParams.put("w_on_hours", String.valueOf(on_time_hours));
                    postDataParams.put("w_on_mins", String.valueOf(on_time_mins));
                    postDataParams.put("w_off_hours", String.valueOf(off_time_hours));
                    postDataParams.put("w_off_mins", String.valueOf(off_time_mins));
                }
                Log.e("params",postDataParams.toString());

                System.setProperty("http.keepAlive", "false");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("connection", "close");
                //conn.setRequestProperty("connection","Keep-Alive");
                conn.setChunkedStreamingMode(0);
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.flush();
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();
                //conn.disconnect();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line="";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    conn.disconnect();
                    return sb.toString();
                } else {
                    conn.disconnect();
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            int temp;
            if ((result != null) && (result.startsWith("false", 0) == false) && (result.startsWith("Exception", 0) == false)) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    if (url_select == 0) {
                        pump_state = jsonObj.getInt("pump");
                        fans_state = jsonObj.getInt("fans");
                        FanState = jsonObj.getInt("fan");
                    }
                    if (url_select == 1) {
                        on_minutes = jsonObj.getInt("time_on");
                        off_minutes = jsonObj.getInt("time_off");
                        on_time_hours = jsonObj.getInt("w_on_hours");
                        on_time_mins = jsonObj.getInt("w_on_mins");
                        off_time_hours = jsonObj.getInt("w_off_hours");
                        off_time_mins = jsonObj.getInt("w_off_mins");
                        mins_on.setText(Integer.toString(on_minutes));
                        mins_off.setText(Integer.toString(off_minutes));
                        if (on_time_mins >= 10)
                            time_on.setText(Integer.toString(on_time_hours) + ":" + Integer.toString(on_time_mins));
                        else
                            time_on.setText(Integer.toString(on_time_hours) + ":0" + Integer.toString(on_time_mins));

                        if (off_time_mins >= 10)
                            time_off.setText(Integer.toString(off_time_hours) + ":" + Integer.toString(off_time_mins));
                        else
                            time_off.setText(Integer.toString(off_time_hours) + ":0" + Integer.toString(off_time_mins));
                    }
                    updateDisplay();
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), " Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                //--------------------------------------------------------------
                jsCounter = 0;
                if (result == null) {
                    jsCounter = 1;
                    Log.e(TAG, "Couldn't get json from server. Result = null");
                }
                if (result.startsWith("false",0) == true) {
                    jsCounter = 2;
                    Log.e(TAG, "Couldn't get json from server. Result = false");
                }
                if (result.startsWith("Exception",0) == true) {
                    jsCounter = 3;
                    Log.e(TAG, "Couldn't get json from server. Result = Exception");
                }
            }
            AsyncRunning = false;
        }

    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while(itr.hasNext()){
            String key= itr.next();
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