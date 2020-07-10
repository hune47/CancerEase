package com.graduation.CancerEaseProj.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.graduation.CancerEaseProj.Adapters.AppointmentsAdapter;
import com.graduation.CancerEaseProj.Models.Appointment;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class AppointmentsActivity extends AppCompatActivity {
    private static final String TAG = "CancerEaseLog: Appoint";
    final Context context = this;
    private SharedPref sharedPref;
    private ProgressBar progressBar;
    private AppointmentsAdapter appointmentsAdapter;
    private FirebaseFirestore dbRef;
    private String accountType, ID;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        initializeItems();
        loadList();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);

        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
            ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            accountType = PATIENTS_COLLECTION;
        }else {
            setTitle("     خدمات الطبيب");
            if (getIntent().hasExtra("patientEmail")){
                ID = getIntent().getStringExtra("patientEmail");
                accountType = PATIENTS_COLLECTION;
                Log.i(TAG, "ID: " + ID);
            }else {
                ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                accountType = DOCTORS_COLLECTION;
            }
        }
        dbRef = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progress_bar);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadList(){
        CollectionReference appointmentsRef;
        TextView emptyView = findViewById(R.id.empty_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CollectionReference patientsRef = dbRef.collection(PATIENTS_COLLECTION);

        if (accountType.equals(PATIENTS_COLLECTION)) {
             appointmentsRef =  dbRef.collection(PATIENTS_COLLECTION)
                    .document(ID).collection("appointments");

        }else {
             appointmentsRef =  dbRef.collection(DOCTORS_COLLECTION)
                    .document(ID).collection("appointments");
        }
        Query query = appointmentsRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Appointment> appointmentRecyclerOptions = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();
        appointmentsAdapter = new AppointmentsAdapter(appointmentRecyclerOptions ,progressBar, emptyView, this);
        recyclerView.setAdapter(appointmentsAdapter);
        appointmentsAdapter.setOnClickListener(new AppointmentsAdapter.OnClickListener() {
            @Override
            public void onClick(Appointment appointment) {
                detailsDialog(appointment);
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void detailsDialog(Appointment appointment) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_appointment_details, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        TextView clinicTxt = mView.findViewById(R.id.clinic_txt);
        TextView nameTxt = mView.findViewById(R.id.name_txt);
        TextView timeTxt = mView.findViewById(R.id.time_txt);
        TextView dateTxt = mView.findViewById(R.id.date_txt);
        TextView dayTxt = mView.findViewById(R.id.day_txt);
        TextView confirmedTxt = mView.findViewById(R.id.confirmed_txt);
        TextView nameTitleTxt = mView.findViewById(R.id.name_title_txt);

        clinicTxt.setText(appointment.getClinic());
        nameTxt.setText(appointment.getName());

        if (appointment.getTimestamp() != null){
            Date messageTime = appointment.getTimestamp().toDate();
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            DateFormat timeFormat = SimpleDateFormat.getTimeInstance( DateFormat.SHORT,Locale.US);
            String messageDateStr = dateFormat.format(messageTime);
            String messageTimeStr = timeFormat.format(messageTime);
            String messageDayStr =  Utils.dayName(messageTime.getDay());
            dateTxt.setText(messageDateStr);
            timeTxt.setText(messageTimeStr);
            dayTxt.setText(messageDayStr);
        }

        if (appointment.getConfirmed() == 1){
            confirmedTxt.setTextColor(getResources().getColor(R.color.green));
            confirmedTxt.setText(getString(R.string.confirmed_appointment));
        }else {
            confirmedTxt.setTextColor(getResources().getColor(R.color.red));
            confirmedTxt.setText( getString(R.string.waiting_appointment));
        }

        if (accountType.equals(DOCTORS_COLLECTION)){
            nameTitleTxt.setText(context.getResources().getString(R.string.name));
        }else {
            nameTitleTxt.setText(context.getResources().getString(R.string.doctor));
        }

        alert.setCancelable(false);

        alert.setNegativeButton("إغلاق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        appointmentsAdapter.startListening();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        appointmentsAdapter.stopListening();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
