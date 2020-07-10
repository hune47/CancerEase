package com.graduation.CancerEaseProj.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.graduation.CancerEaseProj.Utilities.Constants.DEVICE_USER_PHOTOS_PATH;

public class PatientActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 1122;
    public static final String TAG = "CancerEaseLog: Patient";
    private SharedPref sharedPref;
    private TextView nameTxt, recordTxt, ageTxt, genderTxt;
    private CircleImageView patientImage;
    private ProgressDialog progressDialog;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        initializeItems();
        getProfileData();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("خدمات المرضى");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout diagnosis = findViewById(R.id.main_diagnosis_layout);
        LinearLayout health = findViewById(R.id.main_my_health_layout);
        LinearLayout prescriptions = findViewById(R.id.main_prescriptions_layout);
        LinearLayout appointment = findViewById(R.id.main_appointment_layout);
        LinearLayout messages = findViewById(R.id.main_messages_layout);
        LinearLayout reports = findViewById(R.id.main_reports_layout);
        LinearLayout chat = findViewById(R.id.main_chat_layout);
        LinearLayout education= findViewById(R.id.main_education_layout);

        sharedPref = new SharedPref(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.msg_sign_out));
        progressDialog.setCancelable(false);

        nameTxt = findViewById(R.id.patient_name);
        recordTxt = findViewById(R.id.record_no);
        ageTxt = findViewById(R.id.patient_age);
        genderTxt = findViewById(R.id.patient_gender);
        patientImage = findViewById(R.id.profile_image);

        Button accountInfoBtn = findViewById(R.id.account_info_btn);
        accountInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openProfileActivity = new Intent(PatientActivity.this, ProfileActivity.class);
                startActivity(openProfileActivity);
            }
        });

        diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelfDiagnosisActivity.class);
                startActivity(intent);
            }
        });
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyHealthActivity.class);
                startActivity(intent);
            }
        });
        prescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PrescriptionsActivity.class);
                startActivity(intent);
            }
        });
        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
                startActivity(intent);
            }
        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                startActivity(intent);
            }
        });
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
                startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatGroupsActivity.class);
                startActivity(intent);
            }
        });
        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EducationActivity.class);
                startActivity(intent);
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getProfileData(){
        nameTxt.setText(sharedPref.getYourName());
        recordTxt.setText(sharedPref.getYourRecordNumber());
        genderTxt.setText(sharedPref.getYourGender());

        String age = getAge(sharedPref.getYourBirthDate());
        ageTxt.setText(age);
        checkPermissionForPhoto();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkPermissionForPhoto() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(PatientActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(PatientActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {
            File imgFile = new File(DEVICE_USER_PHOTOS_PATH + sharedPref.getYourPhoto());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                patientImage.setImageBitmap(myBitmap);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String getAge(String myDob){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        try {
            date = format.parse(myDob);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar dob = Calendar.getInstance(); //15-08-1994
        Calendar today = Calendar.getInstance(); // 23/2/2020

        dob.setTime(date);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == REQUEST_WRITE_STORAGE) {
                    File imgFile = new File(DEVICE_USER_PHOTOS_PATH + sharedPref.getYourPhoto());
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        patientImage.setImageBitmap(myBitmap);
                    }
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        getProfileData();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu_btn) {
            progressDialog.show();
            Utils.signOut(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}

