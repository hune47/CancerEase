package com.graduation.CancerEaseProj.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.graduation.CancerEaseProj.Adapters.ReportsAdapter;
import com.graduation.CancerEaseProj.Models.Report;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class ReportsActivity extends AppCompatActivity {
    private static final String TAG = "CancerEaseLog: Reports";
    final Context context = this;
    private SharedPref sharedPref;
    private ProgressBar progressBar;
    private ReportsAdapter reportsAdapter;
    private FirebaseFirestore dbRef;
    private String accountType, ID;
    private RelativeLayout reportsListLayout;
    private LinearLayout doctorCnlLayout,addReportLayout;
    private CollectionReference reportsRef;
    private RecyclerView recyclerView;
    private TextView emptyView, reportDateTxt;
    private Button addReportBtn, showReportsBtn, sendReportBtn;
    private EditText reportTitleEdt, reportdetailsEdt;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        initializeItems();
        loadList();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);
        reportsListLayout = findViewById(R.id.reports_list_layout);
        doctorCnlLayout = findViewById(R.id.doctor_cnl_layout);
        addReportLayout = findViewById(R.id.add_report_layout);
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        reportDateTxt = findViewById(R.id.report_date_txt);
        progressBar = findViewById(R.id.progress_bar);
        addReportBtn = findViewById(R.id.add_report_btn);
        showReportsBtn = findViewById(R.id.show_reports_btn);
        sendReportBtn = findViewById(R.id.send_report_btn);
        reportTitleEdt = findViewById(R.id.report_title_edt);
        reportdetailsEdt = findViewById(R.id.report_details_edt);
        dbRef = FirebaseFirestore.getInstance();

        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
            ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            accountType = PATIENTS_COLLECTION;
            reportsListLayout.setVisibility(View.VISIBLE);
        }else {
            setTitle("     خدمات الطبيب");
            if (getIntent().hasExtra("patientEmail")){
                ID = getIntent().getStringExtra("patientEmail");
                doctorCnlLayout.setVisibility(View.VISIBLE);
                accountType = PATIENTS_COLLECTION;
            }
        }
        reportsRef =  dbRef.collection(PATIENTS_COLLECTION).document(ID).collection("reports");

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        showReportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportsListLayout.setVisibility(View.VISIBLE);
                addReportLayout.setVisibility(View.GONE);
            }
        });
        addReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportsListLayout.setVisibility(View.GONE);
                addReportLayout.setVisibility(View.VISIBLE);
                Date date = new Date();
                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String reportDateStr = dateFormat.format(date);
                reportDateTxt.setText(reportDateStr);
            }
        });
        sendReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadList(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = reportsRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Report> reportsRecyclerOptions = new FirestoreRecyclerOptions.Builder<Report>()
                .setQuery(query, Report.class).build();
        reportsAdapter = new ReportsAdapter(reportsRecyclerOptions ,progressBar, emptyView, this);
        recyclerView.setAdapter(reportsAdapter);
        reportsAdapter.setOnClickListener(new ReportsAdapter.OnClickListener() {
            @Override
            public void onClick(Report report) {
                detailsDialog(report);
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendReport(){
        progressBar.setVisibility(View.VISIBLE);
        String title, details;
        title = reportTitleEdt.getText().toString();
        details = reportdetailsEdt.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(details)) {
            Toast.makeText(context, "يرجى تعبئة حقول الإدخال المطلوبة!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else {
            Report report = new Report();
            report.setDoctor_name(sharedPref.getYourName());
            report.setTitle(title);
            report.setReport(details);
            reportsRef.add(report).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(context, "تم ارسال التقرير بنجاح", Toast.LENGTH_SHORT).show();
                    reportdetailsEdt.setText("");
                    reportTitleEdt.setText("");
                    addReportLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "خطأ أثناء إرسال التقرير!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void detailsDialog(Report report) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_report_details, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        TextView reportTitleTxt = mView.findViewById(R.id.report_title_txt);
        TextView nameTitleTxt = mView.findViewById(R.id.name_title_txt);
        TextView doctorNameTxt = mView.findViewById(R.id.doctor_name_txt);
        TextView dateTxt = mView.findViewById(R.id.date_txt);
        TextView reportDetailsTxt = mView.findViewById(R.id.report_details_txt);

        reportTitleTxt.setText(report.getTitle());
        doctorNameTxt.setText(report.getDoctor_name());
        reportDetailsTxt.setText(report.getReport());

        if (report.getTimestamp() != null){
            Date reportTime = report.getTimestamp().toDate();
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String reportDateStr = dateFormat.format(reportTime);
            dateTxt.setText(reportDateStr);
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
        reportsAdapter.startListening();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        reportsAdapter.stopListening();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
