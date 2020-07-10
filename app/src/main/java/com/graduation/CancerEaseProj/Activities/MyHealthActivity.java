package com.graduation.CancerEaseProj.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.graduation.CancerEaseProj.Activities.SleepTracker.SleepQualityActivity;
import com.graduation.CancerEaseProj.Models.DoctorNotification;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.MySingleton;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.FCM_API;
import static com.graduation.CancerEaseProj.Utilities.Constants.SERVER_KEY;
import static com.graduation.CancerEaseProj.Utilities.Constants.EMPATICA_API_KEY;
import static com.graduation.CancerEaseProj.Utilities.Constants.HEALTH_REF;

public class MyHealthActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate, SensorEventListener {
    private static final String TAG = "CancerEaseLog: chealth";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    final Context context = this;
    private String NOTIFICATION_TITLE;
    private String NOTIFICATION_MESSAGE;
    private String TOPIC;
    private EmpaDeviceManager deviceManager = null;
    private SharedPref sharedPref;
    private LinearLayout dataCnt;
    private float stepCount = 0;
    private ImageView sleepImgView;
    private String contentType = "application/json";
    private TextView accel_xLabel, accel_yLabel, accel_zLabel, bvpLabel, edaLabel, ibiLabel, temperatureLabel,
            batteryLabel, statusLabel, deviceNameLabel, tempLabel, respLabel, sleepLabel, stepsLabel, hrvLabel;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_health);
        initializeItems();
        initEmpaticaDeviceManager();
        stepsCounter();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات المرضى");

        sharedPref = new SharedPref(this);
        statusLabel = findViewById(R.id.status);
        dataCnt = findViewById(R.id.dataArea);
        accel_xLabel =  findViewById(R.id.accel_x);
        accel_yLabel = findViewById(R.id.accel_y);
        accel_zLabel =  findViewById(R.id.accel_z);
        bvpLabel =  findViewById(R.id.bvp);
        edaLabel =  findViewById(R.id.eda);
        ibiLabel = findViewById(R.id.ibi);
        hrvLabel = findViewById(R.id.hrv_label);

        temperatureLabel = findViewById(R.id.temperature);
        batteryLabel = findViewById(R.id.battery);
        deviceNameLabel = findViewById(R.id.deviceName);
        tempLabel = findViewById(R.id.temperature_label);
        //respLabel = findViewById(R.id.resp_label);
        //sleepLabel = findViewById(R.id.sleep_label);
        stepsLabel = findViewById(R.id.steps_label);
        sleepImgView = findViewById(R.id.sleep_img);
        sleepImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyHealthActivity.this, SleepQualityActivity.class);
                startActivity(intent);
            }
        });

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final Button disconnectButton = findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceManager != null) {
                    deviceManager.disconnect();
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: permissions " + permissions );
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    Log.i(TAG, "onRequestPermissionsResult: PERMISSION_GRANTED ");
                    initEmpaticaDeviceManager();
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: PERMISSION_GRANTED ");
                    // Permission denied, boo!
                    final boolean needRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION);
                    new AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // try again
                                    if (needRationale) {
                                        // the "never ask again" flash is not set, try again with permission request
                                        initEmpaticaDeviceManager();
                                    } else {
                                        // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setNegativeButton("Exit application", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // without permission exit is the only way
                                    finish();
                                }
                            })
                            .show();
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initEmpaticaDeviceManager() {
        Log.i(TAG, "initEmpaticaDeviceManager" );
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "ACCESS_COARSE_LOCATION: denied" );
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            Log.i(TAG, "ACCESS_COARSE_LOCATION: GRANTED" );
            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                Log.i(TAG, "EMPATICA_API_KEY: Wrong" );
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Please insert your API KEY")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // without permission exit is the only way
                                finish();
                            }
                        })
                        .show();
                return;
            }
            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);
            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: deviceManager: " + deviceManager);
        super.onPause();
        if (deviceManager != null) {
            deviceManager.stopScanning();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: deviceManager: " + deviceManager);
        if (deviceManager != null) {
            deviceManager.cleanUp();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didDiscoverDevice(EmpaticaDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        Log.i(TAG, "didDiscoverDevice: allowed: " + allowed);
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            Log.i(TAG, "allowed: " + allowed);
            deviceManager.stopScanning();
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);
                updateLabel(deviceNameLabel, "To: " + deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(MyHealthActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didFailedScanning(int errorCode) {
        Log.i(TAG, "didFailedScanning: errorCode: " + errorCode);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Log.i(TAG, "didRequestEnableBluetooth");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void bluetoothStateChanged() {
        Log.i(TAG, "bluetoothStateChanged");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "RequestEnableBluetooth: RESULT_CANCELED");
            // You should deal with this
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {
        Log.i(TAG, "didUpdateSensorStatus: status: " + status);
        didUpdateOnWristStatus(status);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didUpdateStatus(EmpaStatus status) {
        Log.i(TAG, "didUpdateStatus: status: " + status);
        // Update the UI
        updateLabel(statusLabel, status.name());
        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            Log.i(TAG, "didUpdateStatus: status: >>>>>>>> READY");
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            // Start scanning
            deviceManager.startScanning();
            // The device manager has established a connection
            hide();
        } else if (status == EmpaStatus.CONNECTED) {
            show();
            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {
            updateLabel(deviceNameLabel, "");
            hide();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
        Log.i(TAG, "didReceiveAcceleration: timestamp: " + timestamp);
        updateLabel(accel_xLabel, "" + x);
        updateLabel(accel_yLabel, "" + y);
        updateLabel(accel_zLabel, "" + z);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        Log.i(TAG, "didReceiveBVP: bvp: " + bvp);
        updateLabel(bvpLabel, "" + bvp);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        Log.i(TAG, "didReceiveBatteryLevel: battery: " + battery);
        updateLabel(batteryLabel, String.format("%.0f %%", battery * 100));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveGSR(float gsr, double timestamp) {
        Log.i(TAG, "didReceiveGSR: gsr: " + gsr);
        updateLabel(edaLabel, "" + gsr);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        Log.i(TAG, "didReceiveIBI: ibi: " + ibi);
        updateLabel(ibiLabel, "" + ibi);
        float hrv = 60/ibi;
        updateLabel(hrvLabel, "" + hrv);
        uploadData("hrv", hrv, timestamp);
        if (hrv<40 || hrv>100){
            sendDoctorNotification(getResources().getString(R.string.go_to_emergency));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveTemperature(float temp, double timestamp) {
        Log.i(TAG, "didReceiveTemperature: temp: " + temp);
        updateLabel(temperatureLabel, "" + temp);
        updateLabel(tempLabel, "" + temp);
        float fTemp = Utils.round(temp,2);
        if (sharedPref.getYourTemp() != fTemp){
            uploadData("temp", temp, timestamp);
            sharedPref.setYourTemp(fTemp);
        }
        if (temp<32 || temp>37){
            sendDoctorNotification(getResources().getString(R.string.go_to_emergency));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Update a label with some text, making sure this is run in the UI thread
    private void updateLabel(final TextView label, final String text) {
        Log.i(TAG, "updateLabel: text: " + text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didReceiveTag(double timestamp) {
        Log.i(TAG, "didReceiveTag: timestamp" + timestamp);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didEstablishConnection() {
        Log.i(TAG, "didEstablishConnection");
        show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {
        Log.i(TAG, "didUpdateOnWristStatus: " + status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == EmpaSensorStatus.ON_WRIST) {
                    ((TextView) findViewById(R.id.wrist_status_label)).setText("ON WRIST");
                }
                else {
                    ((TextView) findViewById(R.id.wrist_status_label)).setText("NOT ON WRIST");
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void show() {
        Log.i(TAG, "show");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*dataCnt.setVisibility(View.VISIBLE);*/
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void hide() {
        Log.i(TAG, "hide");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*dataCnt.setVisibility(View.INVISIBLE);*/
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void stepsCounter(){
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor mSteps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (mSteps!= null){
            sensorManager.registerListener( MyHealthActivity.this, mSteps, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            stepCount =  event.values[0];
            if(stepCount - sharedPref.getYourSteps() >100){
                long timestamp = System.currentTimeMillis();
                uploadData("stepCount", stepCount, timestamp);
                sharedPref.setYourSteps(stepCount);
            }
            updateLabel(stepsLabel, "" + stepCount);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendDoctorNotification(String message){
        TOPIC = "/topics/" + sharedPref.getYourRecordNumber(); //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = sharedPref.getYourName();
        NOTIFICATION_MESSAGE = message;

        final DoctorNotification doctorNotification = new DoctorNotification();
        doctorNotification.setTitle(sharedPref.getYourName());
        doctorNotification.setMessage(message);
        doctorNotification.setEmail(sharedPref.getYourEmail());
        doctorNotification.setRecordNo(sharedPref.getYourRecordNumber());
        doctorNotification.setStatus(1);

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        doctorSaveNotifications(doctorNotification);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "VolleyError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "VolleyError:" + error.getMessage());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", SERVER_KEY);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static void doctorSaveNotifications(final DoctorNotification doctorNotification){
        final CollectionReference doctorsRef = FirebaseFirestore.getInstance().collection(DOCTORS_COLLECTION);
        final String ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.i(TAG,"ID: " + ID);
        Log.i(TAG,"doctorsRef: " + doctorsRef);
        Query query = doctorsRef.whereArrayContains("patients", ID);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i(TAG,"onSuccess" );
                if (queryDocumentSnapshots.size()>0){
                    for (int i = 0; i<queryDocumentSnapshots.size(); i++){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                        User user = documentSnapshot.toObject(User.class);
                        doctorsRef.document(user.getEmail())
                                .collection("notifications")
                                .add(doctorNotification);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"خطأ أثناء الوصول لبيانات الطبيب! :" + e.getMessage());
            }
        });

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void  uploadData(String data, float value, double timestamp) {
        String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef,healthRef,userHealthRef,dataRef,dataKeyRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        healthRef = rootRef.child(HEALTH_REF);
        userHealthRef = healthRef.child(ID);
        dataRef = userHealthRef.child(data);
        String dataKey = dataRef.push().getKey();
        dataKeyRef = dataRef.child(dataKey);
        HashMap<String, Object> dataInfoMap = new HashMap<>();
        dataInfoMap.put("value", value);
        dataInfoMap.put("timestamp", timestamp);
        dataKeyRef.updateChildren(dataInfoMap);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}

