package com.graduation.CancerEaseProj.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.graduation.CancerEaseProj.Models.Prescription;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.AlertReceiver;
import com.graduation.CancerEaseProj.Utilities.DatePicker;
import com.graduation.CancerEaseProj.Utilities.TimePicker;
import com.graduation.CancerEaseProj.Utilities.Utils;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class AlarmsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    public static final String TAG = "CancerEaseLog: alert";
    private ProgressBar progressBar;
    private DialogFragment timePicker, datePicker;
    private EditText startDateEdt, alarmTimeEdt;
    private Calendar startAlarmCalendar;
    private ImageView startDateBtn, alarmTimeBtn;
    private String pres_name;
    private String dosage;
    private CheckBox everyDayCbx;
    private Button saveAlarmBtn, deleteAlarmBtn;
    private DocumentReference prescriptionRef;
    private int A_KEY, ed_alarm;
    private long prevAlarm;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
        initializeItems();
        checkPrevAlarm();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems() {
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات المرضى");

        Prescription prescription = (Prescription) getIntent().getSerializableExtra("prescription");
        String pres_id = prescription.getId();
        pres_name = prescription.getPres_name();
        dosage = prescription.getDosage();
        prevAlarm = prescription.getAlarm();
        A_KEY = prescription.getA_KEY();
        ed_alarm = prescription.getEd_alarm();

        String ID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        CollectionReference patientsRef = dbRef.collection(PATIENTS_COLLECTION);
        assert ID != null;
        prescriptionRef =  patientsRef.document(ID).collection("prescriptions")
                .document(Objects.requireNonNull(pres_id));

        timePicker = new TimePicker();
        datePicker = new DatePicker();
        startAlarmCalendar = Calendar.getInstance();

        startDateEdt = findViewById(R.id.start_date_edt);
        alarmTimeEdt = findViewById(R.id.alarm_time_edt);
        startDateEdt.setEnabled(false);
        alarmTimeEdt.setEnabled(false);

        progressBar = findViewById(R.id.progress_bar);
        everyDayCbx = findViewById(R.id.every_day_cbx);
        startDateBtn = findViewById(R.id.start_date_btn);
        alarmTimeBtn = findViewById(R.id.alert_time_btn);
        saveAlarmBtn = findViewById(R.id.save_alarm_btn);
        deleteAlarmBtn = findViewById(R.id.delete_alarm_btn);

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        alarmTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (alarmTimeEdt.getText().equals("")){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AlarmsActivity.this, "يجب اختيار وقت التنبيه", Toast.LENGTH_SHORT).show();
                }else {
                    saveAlarmTime();
                }
            }
        });
        deleteAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                deleteAlarm();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkPrevAlarm(){
        if ( Utils.compareTime(prevAlarm)|| ed_alarm == 1 ){
            Date alarmDate = new Date(prevAlarm);
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT,Locale.US);
            String messageDateStr = dateFormat.format(alarmDate);
            String messageTimeStr = timeFormat.format(alarmDate);
            startDateEdt.setText(messageDateStr);
            alarmTimeEdt.setText(messageTimeStr);
            deleteAlarmBtn.setVisibility(View.VISIBLE);
            saveAlarmBtn.setVisibility(View.GONE);
            everyDayCbx.setVisibility(View.GONE);
            startDateBtn.setEnabled(false);
            alarmTimeBtn.setEnabled(false);
            Toast.makeText(this, "المنبه مفعل", Toast.LENGTH_SHORT).show();
        }else {
            saveAlarmBtn.setVisibility(View.VISIBLE);
            everyDayCbx.setVisibility(View.VISIBLE);
            deleteAlarmBtn.setVisibility(View.GONE);
            startDateBtn.setEnabled(true);
            alarmTimeBtn.setEnabled(true);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void saveAlarmTime(){
        A_KEY = new Random().nextInt(1000) + 1000;
        ed_alarm = 0;
        long alarm = startAlarmCalendar.getTimeInMillis();
        if (everyDayCbx.isChecked()){
            ed_alarm = 1;
        }
        prescriptionRef.update("alarm", alarm,"A_KEY",A_KEY , "ed_alarm", ed_alarm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        startAlarm();
                        Toast.makeText(AlarmsActivity.this, "تم تعيين المنبه بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AlarmsActivity.this,  " خطأ في حفظ التنبيه! " +  e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void startAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title","تذكير بموعد الدواء");
        intent.putExtra("message",pres_name + " - " + dosage);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, A_KEY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i(TAG, "A_KEY = " + A_KEY);
        if (everyDayCbx.isChecked()){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startAlarmCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, startAlarmCalendar.getTimeInMillis(), pendingIntent);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteAlarm(){
        prescriptionRef.update("alarm", 0,"A_KEY", 0, "ed_alarm", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                stopAlarm();
                Toast.makeText(AlarmsActivity.this,  "تم إلغاء المنبه بنجاح",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AlarmsActivity.this,  " خطأ أثناء إلغاء التنبيه! " +  e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void stopAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, A_KEY, intent, 0);
        alarmManager.cancel(pendingIntent);
        checkPrevAlarm();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        startAlarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        startAlarmCalendar.set(Calendar.MINUTE, minute);
        startAlarmCalendar.set(Calendar.SECOND, 0);
        String timeTxt = DateFormat.getTimeInstance(DateFormat.SHORT).format(startAlarmCalendar.getTime());
        alarmTimeEdt.setText(timeTxt);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        String dateTxt;
        startAlarmCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        startAlarmCalendar.set(Calendar.MONTH, month);
        startAlarmCalendar.set(Calendar.YEAR, year);
        dateTxt = DateFormat.getDateInstance(DateFormat.MEDIUM).format(startAlarmCalendar.getTime());
        startDateEdt.setText(dateTxt);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
