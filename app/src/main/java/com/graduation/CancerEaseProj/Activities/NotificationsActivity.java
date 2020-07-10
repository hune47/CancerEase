package com.graduation.CancerEaseProj.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.graduation.CancerEaseProj.Adapters.NotificationsAdapter;
import com.graduation.CancerEaseProj.Models.DoctorNotification;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.NOTIFICATIONS_COLLECTION;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = "CancerEaseLog: Notify";
    private CollectionReference notificationsRef;
    private CollectionReference doctorsRef;
    private FirebaseFirestore dbRef;
    private NotificationsAdapter notificationsAdapter;
    private ProgressBar progressBar;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initializeItems();
        loadList();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات الطبيب");

        String ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        dbRef = FirebaseFirestore.getInstance();
        doctorsRef = dbRef.collection(DOCTORS_COLLECTION);
        notificationsRef = doctorsRef.document(ID).collection(NOTIFICATIONS_COLLECTION);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
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

        Query query = notificationsRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<DoctorNotification> notificationsRecyclerOptions = new FirestoreRecyclerOptions.Builder<DoctorNotification>()
                .setQuery(query, DoctorNotification.class)
                .build();
        notificationsAdapter = new NotificationsAdapter(notificationsRecyclerOptions ,progressBar, emptyView, this);
        recyclerView.setAdapter(notificationsAdapter);
        notificationsAdapter.setOnClickListener(new NotificationsAdapter.OnClickListener() {
            @Override
            public void onClick(DoctorNotification doctorNotification) {
                notificationMessageDialogue(doctorNotification);
            }
        });
        progressBar.setVisibility(View.GONE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void notificationMessageDialogue(DoctorNotification doctorNotification){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(doctorNotification.getTitle());
        builder.setMessage(doctorNotification.getMessage());
        builder.setPositiveButton("إغلاق",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        notificationsAdapter.startListening();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        notificationsAdapter.stopListening();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
