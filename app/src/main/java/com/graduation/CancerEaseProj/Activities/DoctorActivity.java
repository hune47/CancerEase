package com.graduation.CancerEaseProj.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.graduation.CancerEaseProj.Adapters.DoctorPatientsAdapter;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.graduation.CancerEaseProj.Utilities.Constants.DEVICE_USER_PHOTOS_PATH;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class DoctorActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 1122;
    public static final String TAG = "CancerEaseLog: Doctor";
    private SharedPref sharedPref;
    private TextView nameTxt, recordTxt;
    private CircleImageView doctorImage;
    private ProgressDialog progressDialog;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        initializeItems();
        getProfileData();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getProfileData(){
        nameTxt.setText(sharedPref.getYourName());
        recordTxt.setText(sharedPref.getYourRecordNumber());
        checkPermissionForPhoto();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkPermissionForPhoto() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(DoctorActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(DoctorActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {
            File imgFile = new File(DEVICE_USER_PHOTOS_PATH + sharedPref.getYourPhoto());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                doctorImage.setImageBitmap(myBitmap);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات الطبيب");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout patients = findViewById(R.id.main_patients_layout);
        LinearLayout appointment = findViewById(R.id.main_appointment_layout);
        LinearLayout messages = findViewById(R.id.main_messages_layout);

        sharedPref = new SharedPref(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.msg_sign_out));
        progressDialog.setCancelable(false);

        nameTxt = findViewById(R.id.doctor_name);
        recordTxt = findViewById(R.id.record_no);
        doctorImage = findViewById(R.id.profile_image);

        Button accountInfoBtn = findViewById(R.id.account_info_btn);
        accountInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openProfileActivity = new Intent(DoctorActivity.this, ProfileActivity.class);
                startActivity(openProfileActivity);
            }
        });

        patients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PatientsListActivity.class);
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
                        doctorImage.setImageBitmap(myBitmap);
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
        getMenuInflater().inflate(R.menu.notifications_menu,menu);
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.notifications_menu_btn) {
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.sign_out_menu_btn) {
            progressDialog.show();
            Utils.signOut(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
