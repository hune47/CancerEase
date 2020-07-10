package com.graduation.CancerEaseProj.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.graduation.CancerEaseProj.Adapters.DoctorPatientsAdapter;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class PatientsListActivity extends AppCompatActivity {
    private static final String TAG = "CancerEaseLog: PtList";
    private CollectionReference patientsRef;
    private ProgressBar progressBar;
    private DoctorPatientsAdapter doctorPatientsAdapter;
    private String ID;
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);

        initializeItems();
        loadList();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات الطبيب");
        ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        patientsRef = dbRef.collection(PATIENTS_COLLECTION);
        progressBar = findViewById(R.id.progress_bar);
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openPatientActivity = new Intent(getApplicationContext(), DoctorActivity.class);
                startActivity(openPatientActivity);
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadList(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView emptyView = findViewById(R.id.empty_view);
        Query query = patientsRef.whereArrayContains("doctors", ID).orderBy("recordNumber",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<User> fireStoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        doctorPatientsAdapter = new DoctorPatientsAdapter(fireStoreRecyclerOptions,progressBar,emptyView);
        recyclerView.setAdapter(doctorPatientsAdapter);
        doctorPatientsAdapter.setOnClickListener(new DoctorPatientsAdapter.OnClickListener() {
            @Override
            public void onClick(User user) {
                Intent intent = new Intent(getApplicationContext(), PatientPageActivity.class);
                intent.putExtra("patient", user);
                startActivity(intent);
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        doctorPatientsAdapter.startListening();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        doctorPatientsAdapter.stopListening();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
}
