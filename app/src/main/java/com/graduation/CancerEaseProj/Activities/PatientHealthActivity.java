package com.graduation.CancerEaseProj.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduation.CancerEaseProj.Models.MyHealth;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.graduation.CancerEaseProj.Utilities.Constants.HEALTH_REF;

public class PatientHealthActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: pHealth";
    private Context context = this;
    private SharedPref sharedPref;
    private ImageView stepsBtn, tempBtn, hrvBtn;
    private String ID;
    private DatabaseReference rootRef,healthRef,userHealthRef,dataRef,dataKeyRef;
    private User patient;
    private ProgressDialog progressDialog;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_health);
        initializeItems();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات الطبيب");

        sharedPref = new SharedPref(this);
        patient = (User) getIntent().getSerializableExtra("patient");
        ID = patient.getId();
        Log.i(TAG,"ID: " + ID);
        rootRef = FirebaseDatabase.getInstance().getReference();
        healthRef = rootRef.child(HEALTH_REF);
        userHealthRef = healthRef.child(ID);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.setCancelable(false);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        stepsBtn = findViewById(R.id.steps_btn);
        stepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllValues("stepCount");
            }
        });
        tempBtn = findViewById(R.id.temp_btn);
        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllValues("temp");
            }
        });
        hrvBtn = findViewById(R.id.hrv_btn);
        hrvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllValues("hrv");
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getAllValues(final String data){
        progressDialog.show();
        dataRef = userHealthRef.child(data);
        Query query = dataRef.limitToLast(50);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,Object> items;
                List<MyHealth> myHealths = new ArrayList<>();
                if (dataSnapshot.getValue() != null){
                    items = (Map<String,Object>) dataSnapshot.getValue();
                    for (Map.Entry<String,Object> entry : items.entrySet()){
                        Map item = (Map) entry.getValue();
                        MyHealth myHealth = new MyHealth();

                        double timestamp = 0;
                        double value = 0;
                        if (data.equals("stepCount")){
                            Long l = new Long((long)item.get("timestamp"));
                            timestamp = l.doubleValue();
                            Long l1 = l = new Long((long)item.get("timestamp"));
                            value =  l1.doubleValue();
                        }else {
                            timestamp = (Double) item.get("timestamp");
                            value = (Double) item.get("value");
                        }
                        myHealth.setValue(value);
                        myHealth.setTimestamp(timestamp);
                        myHealths.add(myHealth);
                    }
                    detailsDialog(myHealths);
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "لا يوجد بيانات لعرضها!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void detailsDialog(List<MyHealth> items ) {
        Collections.sort(items, new Comparator<MyHealth>() {
            @Override
            public int compare(MyHealth o1, MyHealth o2) {
                double r = o1.getTimestamp() - o2.getTimestamp();
                Log.i(TAG,"r: " + r);
                return (int) r;
            }
        });
        Log.i(TAG,"items: " + items);

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_graph, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);
        int size = items.size();
        DataPoint[] dataPoints = new DataPoint[size];
        int counter = 0;
        double temp = 0;
        for (MyHealth myHealth : items){
            if (myHealth.getTimestamp()>temp){
                temp = myHealth.getTimestamp();

                dataPoints[counter] = new DataPoint( myHealth.getTimestamp(),myHealth.getValue());
                Log.i(TAG,"timestamp: " + myHealth.getTimestamp());
            }else {
                dataPoints[counter] = new DataPoint(temp,myHealth.getValue());
            }
            counter++;
        }
        Log.i(TAG,"dataPoints: " + dataPoints);
        final GraphView graph = mView.findViewById(R.id.graph);
        try {
            LineGraphSeries<DataPoint> series = new LineGraphSeries < > (dataPoints);
            graph.addSeries(series);
            graph.getGridLabelRenderer().setTextSize(18);

        } catch (IllegalArgumentException e) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    long time = (long) value;
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(time * 1000);
                    String date = DateFormat.format("d-M  hh:mm:ss", cal).toString();
                    return date;
                }else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        progressDialog.dismiss();
        alert.setCancelable(true);
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
