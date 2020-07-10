package com.graduation.CancerEaseProj.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.graduation.CancerEaseProj.Adapters.PrescriptionsAdapter;
import com.graduation.CancerEaseProj.Models.Prescription;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.TimePicker;
import com.graduation.CancerEaseProj.Utilities.Utils;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class PrescriptionsActivity extends AppCompatActivity{
    public static final String TAG = "CE_TAG: Prescriptions";
    private SharedPref sharedPref;
    private ProgressBar progressBar;
    private PrescriptionsAdapter prescriptionsAdapter;
    private FirebaseFirestore dbRef;
    private String ID;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        initializeItems();
        loadList();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems() {
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);
        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
            ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }else {
            setTitle("     خدمات الطبيب");
            ID = getIntent().getStringExtra("patientEmail");
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
        TextView emptyView = findViewById(R.id.empty_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference prescriptionRef =  dbRef.collection(PATIENTS_COLLECTION)
                .document(ID).collection("prescriptions");

        Query query = prescriptionRef.orderBy("pres_name",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Prescription> fireStoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Prescription>()
                .setQuery(query,Prescription.class)
                .build();

        prescriptionsAdapter = new PrescriptionsAdapter(fireStoreRecyclerOptions,progressBar, emptyView, sharedPref.getAccountType());
        recyclerView.setAdapter(prescriptionsAdapter);

        prescriptionsAdapter.setOnClickListener(new PrescriptionsAdapter.OnClickListener() {
            @Override
            public void onClick(Prescription prescription) {
                Intent intent = new Intent(getApplicationContext(), AlarmsActivity.class);
                intent.putExtra("prescription", prescription);
                startActivity(intent);
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        prescriptionsAdapter.startListening();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        prescriptionsAdapter.stopListening();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
