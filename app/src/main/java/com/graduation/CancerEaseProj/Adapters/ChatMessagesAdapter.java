package com.graduation.CancerEaseProj.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.graduation.CancerEaseProj.Models.ChatMessage;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.graduation.CancerEaseProj.Utilities.Constants.CLOUD_USER_PHOTOS_DIR;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder> {
    private Context context;
    private List<ChatMessage> chatMessages;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsgText;
        TextView senderMsgText;
        CircleImageView chatImage;

        public ViewHolder(View view) {
            super(view);
            senderMsgText = view.findViewById(R.id.sender_msg_text);
            receiverMsgText = view.findViewById(R.id.receiver_msg_text);
            chatImage = view.findViewById(R.id.chat_prof_img);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ChatMessagesAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(itemView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ChatMessage currentChatMessage = chatMessages.get(position);

        String messageType = currentChatMessage.getType();
        String ID = mAuth.getCurrentUser().getEmail();
        String senderId = currentChatMessage.getSender_id();

        if (messageType.equals("text")){
            holder.chatImage.setVisibility(View.INVISIBLE);
            holder.receiverMsgText.setVisibility(View.INVISIBLE);
            holder.senderMsgText.setVisibility(View.INVISIBLE);
            if (ID.equals(senderId)){
                holder.senderMsgText.setVisibility(View.VISIBLE);
                holder.senderMsgText.setText(currentChatMessage.getMessage());
            }else {
                holder.receiverMsgText.setVisibility(View.VISIBLE);
                holder.receiverMsgText.setText(currentChatMessage.getMessage());
                holder.chatImage.setVisibility(View.VISIBLE);
                DocumentReference patientRef =  FirebaseFirestore.getInstance()
                        .collection(PATIENTS_COLLECTION).document(senderId);
                patientRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    String url = user.getPhoto();
                                    if (!TextUtils.isEmpty(url))
                                    Picasso.get().load(url).into(holder.chatImage);
                                }
                            }
                    }
                });
            }
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public long getItemId(int position) {
        return position;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
