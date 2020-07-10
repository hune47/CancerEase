package com.graduation.CancerEaseProj.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graduation.CancerEaseProj.Adapters.ChatMessagesAdapter;
import com.graduation.CancerEaseProj.Models.ChatGroup;
import com.graduation.CancerEaseProj.Models.ChatMessage;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import static com.graduation.CancerEaseProj.Utilities.Constants.CHAT_GROUPS_REF;

public class ChatActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: chatM";
    private SharedPref sharedPref;
    private Button sentBtn;
    private EditText messageEdt;
    private DatabaseReference rootRef, groupRef, messageKeyRef;
    private String groupName ,ID, name, currDate, currTime;
    private String groupId;
    private RecyclerView recyclerView;
    private ChatMessagesAdapter chatMessagesAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeItems();
        displayGroupMessages();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        setTitle("     خدمات المرضى");

        ChatGroup chatGroup = (ChatGroup) getIntent().getSerializableExtra("chatGroup");
        groupId = String.valueOf(chatGroup.getId());
        groupName = chatGroup.getName();
        String mTitle = "محاربي " + groupName;
        TextView tabTitle = findViewById(R.id.tab_title);
        tabTitle.setText(mTitle);

        sharedPref = new SharedPref(this);
        String fullName = sharedPref.getYourName();
        int i = fullName.indexOf(' ');
        name = fullName.substring(0, i);

        ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        rootRef = FirebaseDatabase.getInstance().getReference();
        groupRef = rootRef.child(CHAT_GROUPS_REF).child(groupId);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatMessagesAdapter = new ChatMessagesAdapter(this, chatMessages);
        recyclerView.setAdapter(chatMessagesAdapter);

        sentBtn = findViewById(R.id.send_msg_btn);
        messageEdt = findViewById(R.id.input_message_edt);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                messageEdt.setText("");
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void  sendMessage() {
        String message = messageEdt.getText().toString();
        String messageKey = groupRef.push().getKey();

        if (!TextUtils.isEmpty(message)) {
            Calendar calDate = Calendar.getInstance();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currDate = currDateFormat.format(calDate.getTime());

            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat currTimeFormat = new SimpleDateFormat("hh:mm a");
            currTime = currTimeFormat.format(calTime.getTime());
            messageKeyRef = groupRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("sender_id", ID);
            messageInfoMap.put("sender_name", name);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currDate);
            messageInfoMap.put("time", currTime);
            messageInfoMap.put("type", "text");
            messageKeyRef.updateChildren(messageInfoMap);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void displayGroupMessages(){
        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                chatMessages.add(chatMessage);
                chatMessagesAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
