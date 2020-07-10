package com.graduation.CancerEaseProj.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.graduation.CancerEaseProj.Models.QResult;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class PatientPageActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: PatientH";
    final Context context = this;
    private TextView nameTxt, recordTxt, ageTxt, genderTxt;
    private CircleImageView patientImage;
    private ProgressDialog progressDialog;
    private User patient;
    private String recommendations;
    private int physicalTScale, psychologicalTScale, socialTScale, environmentTScale;
    private boolean rep_msg = false;
    private FirebaseFirestore dbRef;
    private DocumentReference patientRef;
    private CollectionReference questionnaireRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_page);
        initializeItems();
        getProfileData();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("خدمات الطبيب");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        patient = (User) getIntent().getSerializableExtra("patient");

        LinearLayout diagnosis = findViewById(R.id.main_diagnosis_layout);
        LinearLayout health = findViewById(R.id.main_my_health_layout);
        LinearLayout prescriptions = findViewById(R.id.main_prescriptions_layout);
        LinearLayout appointment = findViewById(R.id.main_appointment_layout);
        LinearLayout reports = findViewById(R.id.main_reports_layout);
        LinearLayout education= findViewById(R.id.main_education_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.setCancelable(false);
        progressDialog.show();

        nameTxt = findViewById(R.id.patient_name);
        recordTxt = findViewById(R.id.record_no);
        ageTxt = findViewById(R.id.patient_age);
        genderTxt = findViewById(R.id.patient_gender);
        patientImage = findViewById(R.id.profile_image);

        dbRef = FirebaseFirestore.getInstance();
        patientRef = dbRef.collection(PATIENTS_COLLECTION).document(patient.getEmail());

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                questionnaireRef =  dbRef.collection(PATIENTS_COLLECTION)
                        .document(patient.getEmail()).collection("questionnaire");
                Query query = questionnaireRef.orderBy("timestamp",Query.Direction.DESCENDING).limit(1);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot d = queryDocumentSnapshots.getDocuments().get(0);
                        physicalTScale = Integer.parseInt(d.get("physical").toString());
                        psychologicalTScale = Integer.parseInt(d.get("psychological").toString());
                        socialTScale = Integer.parseInt(d.get("social").toString());
                        environmentTScale = Integer.parseInt(d.get("environment").toString());
                        Log.i(TAG,"physical: " +  physicalTScale + " psychological: " +  psychologicalTScale
                        +" social: " +  socialTScale +" environment: " +  environmentTScale );
                        detailsDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "خطأ أثناء الوصول لبيانات المريض!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PatientHealthActivity.class);
                intent.putExtra("patient", patient);
                startActivity(intent);
            }
        });
        prescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PrescriptionsActivity.class);
                intent.putExtra("patientEmail", patient.getEmail());
                startActivity(intent);
            }
        });
        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
                intent.putExtra("patientEmail", patient.getEmail());
                startActivity(intent);
            }
        });
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
                intent.putExtra("patientEmail", patient.getEmail());
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
        nameTxt.setText(patient.getName());
        recordTxt.setText(patient.getRecordNumber());
        genderTxt.setText(patient.getGender());

        patientRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        String url = user.getPhoto();
                        if (!TextUtils.isEmpty(user.getPhoto())) {
                            showImage(url);
                        }else {
                            progressDialog.dismiss();
                        }
                    }else {
                        progressDialog.dismiss();
                        Log.i(TAG, "Can't found user!");
                    }
                }else {
                    Log.i(TAG, "Can't found user!");
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.i(TAG, "Exception: getUserData: " + e.getMessage());
            }
        });

        String age = getAge(patient.getBirthDate());
        ageTxt.setText(age);
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
    private void showImage(String url){
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                patientImage.setImageBitmap(bitmap);
                progressDialog.dismiss();
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                progressDialog.dismiss();
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.get().load(url).into(target);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void detailsDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_questionnaire_details, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        TextView physicalTxt = mView.findViewById(R.id.physical_txt);
        TextView psychologicalTxt = mView.findViewById(R.id.psychological_txt);
        TextView socialTxt = mView.findViewById(R.id.social_txt);
        TextView environmentTxt = mView.findViewById(R.id.environment_txt);
        TextView recomTxt =  mView.findViewById(R.id.recom_text);

        ImageView physicalBar = mView.findViewById(R.id.physical_bar);
        ImageView psychologicalBar = mView.findViewById(R.id.psychological_bar);
        ImageView socialBar = mView.findViewById(R.id.social_bar);
        ImageView environmentBar = mView.findViewById(R.id.environment_bar);
        recommendations = "";

        result(physicalTScale, physicalTxt,physicalBar, 1);
        result(psychologicalTScale, psychologicalTxt,psychologicalBar, 2);
        result(socialTScale, socialTxt,socialBar, 3);
        result(environmentTScale, environmentTxt,environmentBar, 4);
        if (recommendations.isEmpty()){
            recommendations = getResources().getString(R.string.no_recommendations );
        }
        recomTxt.setText(recommendations);
        alert.setCancelable(false);

        alert.setNegativeButton("إغلاق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        progressDialog.dismiss();
        alertDialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void result(int domTotal, TextView domTextView, ImageView bar,int domain){
        int level = 1;
        if (domain == 1){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal < 14 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                recommendations += "- " +  getResources().getString(R.string.go_to_emergency ) + "\n";
            }else  if (domTotal < 28){
                level = 2;
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);

            }else {
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "physical totalAverage: " + domTotal);
        }else if (domain == 2){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal < 12 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                recommendations += "- " +  getResources().getString(R.string.go_to_psychiatrist) + "\n";
            }else  if (domTotal < 24){
                level = 2;
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);
            }else {
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "psychological totalAverage: " + domTotal);
        }else if (domain == 3){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal < 6 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                rep_msg = true;
                recommendations += "- " +  getResources().getString(R.string.go_to_hospital) + "\n";
            }else  if (domTotal < 12){
                level = 2;
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);
            }else{
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "social totalAverage: " + domTotal);
        }else if (domain == 4){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal <= 16 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                if (!rep_msg){
                    recommendations += "- " +  getResources().getString(R.string.go_to_hospital);
                }
            }else  if (domTotal < 32){
                level = 2;
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);
            }else{
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "environment totalAverage: " + domTotal);
        }
        bar.getLayoutParams().height = domTotal*7;
        QResult qResult = new QResult();
        qResult.setDomain_id(domain);
        qResult.setLevel(level);
        qResult.setResult(domTotal);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
