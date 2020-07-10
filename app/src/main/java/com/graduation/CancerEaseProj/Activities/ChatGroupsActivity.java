package com.graduation.CancerEaseProj.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduation.CancerEaseProj.Adapters.ChatGroupsAdapter;
import com.graduation.CancerEaseProj.Models.ChatGroup;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.DBHelper;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import java.util.List;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.CHAT_GROUPS_REF;

public class ChatGroupsActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: chatG";
    private SharedPref sharedPref;
    private DatabaseReference rootRef;
    private String ID;
    private ProgressBar progressBar;
    private DBHelper database;
    private RecyclerView recyclerView;
    private ChatGroup mChatGroup;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);
        initializeItems();
        loadChatGroups();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);
        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
        }else {
            setTitle("     خدمات الطبيب");
        }

        rootRef = FirebaseDatabase.getInstance().getReference();
        ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        database = new DBHelper(this);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadChatGroups(){
        List<ChatGroup> chatGroupList = database.getAllGroups();
        Log.i(TAG,"chatGroupList: " + chatGroupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatGroupsAdapter  chatGroupsAdapter = new ChatGroupsAdapter(this, chatGroupList);
        recyclerView.setAdapter(chatGroupsAdapter);

        chatGroupsAdapter.setOnClickListener(new ChatGroupsAdapter.OnClickListener() {
            @Override
            public void onClick(ChatGroup chatGroup) {
                mChatGroup = chatGroup;
                int enrolled = chatGroup.getEnrolled();
                if (enrolled == 1){
                    startChatActivity();
                }else {
                    confirmationMsg();
                }

            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void confirmationMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("مجموعة " + mChatGroup.getName() );
        builder.setMessage(getResources().getString(R.string.enroll_conf_msg ) );
        builder.setPositiveButton("انضمام للمجموعة",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enroll();
                    }
                });
        builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void  enroll(){
        mChatGroup.setEnrolled(1);
        database.updateGroup(mChatGroup);
        startChatActivity();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void startChatActivity(){
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("chatGroup", mChatGroup);
        startActivity(intent);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
