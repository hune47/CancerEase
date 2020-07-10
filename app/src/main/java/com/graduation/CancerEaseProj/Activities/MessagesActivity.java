package com.graduation.CancerEaseProj.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.MESSAGES_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.graduation.CancerEaseProj.Adapters.UserMessagesAdapter;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.Models.UserMessage;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;

public class MessagesActivity extends AppCompatActivity {
    private static final String TAG = "CancerEaseLog: Messages";
    final Context context = this;
    private CollectionReference messagesRef;
    private CollectionReference patientsRef;
    private CollectionReference doctorsRef;
    private CollectionReference senderRef,receiverRef;
    private DocumentReference receiverID;
    private UserMessagesAdapter userMessagesAdapter;
    private SharedPref sharedPref;
    private String receiver;
    private String subject;
    private String  message;
    private ProgressBar progressBar;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        initializeItems();
        loadList();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);
        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
        }else {
            setTitle("     خدمات الطبيب");
        }
        String ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        messagesRef = dbRef.collection(MESSAGES_COLLECTION);
        patientsRef = dbRef.collection(PATIENTS_COLLECTION);
        doctorsRef = dbRef.collection(DOCTORS_COLLECTION);
        senderRef = messagesRef.document(ID).collection("userMessages");

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
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView emptyView = findViewById(R.id.empty_view);

        Query query = senderRef.orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserMessage> fireStoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<UserMessage>()
                .setQuery(query,UserMessage.class)
                .build();

        userMessagesAdapter = new UserMessagesAdapter(fireStoreRecyclerOptions,progressBar,emptyView);
        recyclerView.setAdapter(userMessagesAdapter);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void messageDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.input_message_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final EditText receiverEdt = mView.findViewById(R.id.input_msg_to);
        final EditText subjectEdt = mView.findViewById(R.id.input_msg_subject);
        final EditText messageEdt  = mView.findViewById(R.id.input_msg);

        alert.setCancelable(false);
        alert.setPositiveButton("إرسال", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                 receiver = receiverEdt.getText().toString();
                 subject = subjectEdt.getText().toString();
                 message = messageEdt.getText().toString();

                if (receiver.trim().isEmpty() || subject.trim().isEmpty()) {
                    Toast.makeText(context, "مطلوب تحديد المستلم وموضوع الرسالة!", Toast.LENGTH_SHORT).show();
                } else {
                    newMessage();
                }
            }
        });
        alert.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void newMessage(){
        progressBar.setVisibility(View.VISIBLE);
        receiverRef = messagesRef.document(receiver).collection("userMessages");
        receiverID = patientsRef.document(receiver);
        receiverID.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    sendMessage(documentSnapshot);
                }else {
                    receiverID = doctorsRef.document(receiver);
                    receiverID.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            if (documentSnapshot.exists()) {
                                sendMessage(documentSnapshot);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                receiverID = doctorsRef.document(receiver);
                                Log.i(TAG, "المستخدم غير موجود!");
                                Toast.makeText(MessagesActivity.this,"المستخدم غير موجود!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.i(TAG, "onFailure: " +  e.getMessage());
                            Toast.makeText(MessagesActivity.this, "onFailure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onFailure: " +  e.getMessage());
                Toast.makeText(MessagesActivity.this, "onFailure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendMessage(DocumentSnapshot documentSnapshot){
        final String messageId = receiverRef.document().getId();
        User user = documentSnapshot.toObject(User.class);
        if (user != null) {
            final UserMessage userMessage = new UserMessage(subject,message,sharedPref.getYourName(), user.getName(),0,1);
            receiverRef.document(messageId).set(userMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    userMessage.setType(0);
                    senderRef.document(messageId).set(userMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "تم الارسال بنجاح", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "فشل ارسال الرسال .. يرجى التحقق من البيانات المدخلة!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_menu,menu);
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_message_menu_btn) {
            messageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        userMessagesAdapter.startListening();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        userMessagesAdapter.stopListening();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
