package com.graduation.CancerEaseProj.Adapters;

import android.icu.text.TimeZoneFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.graduation.CancerEaseProj.Models.UserMessage;
import com.graduation.CancerEaseProj.R;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserMessagesAdapter extends FirestoreRecyclerAdapter<UserMessage,UserMessagesAdapter.MyHolder> {
    private ProgressBar progressBar;
    private TextView emptyTextView;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public UserMessagesAdapter(@NonNull FirestoreRecyclerOptions<UserMessage> options, ProgressBar progressBar, TextView emptyTextView) {
        super(options);
        this.progressBar = progressBar;
        this.emptyTextView = emptyTextView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull UserMessage model) {

        holder.subjectTxt.setText(model.getSubject());
        holder.messageDetailsTxt.setText(model.getMessage());
        if (model.getType()==0){
            holder.senderTxt.setText(model.getReceiver());
        }else {
            holder.senderTxt.setText(model.getSender());
        }
        if (model.getTimestamp() != null){
            Date timestamp = model.getTimestamp().toDate();
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            DateFormat timeFormat = SimpleDateFormat.getTimeInstance( DateFormat.SHORT,Locale.US);
            String messageDateStr = dateFormat.format(timestamp);
            String messageTimeStr = timeFormat.format(timestamp);
            holder.messageDateTxt.setText(messageDateStr);
            holder.messageTimeTxt.setText(messageTimeStr);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messages_list,parent,false);

        return new MyHolder(v);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class MyHolder extends RecyclerView.ViewHolder{
        TextView senderTxt, subjectTxt, messageDetailsTxt, messageDateTxt, messageTimeTxt;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            senderTxt = itemView.findViewById(R.id.message_sender);
            subjectTxt = itemView.findViewById(R.id.message_subject);
            messageDetailsTxt = itemView.findViewById(R.id.message_details);
            messageDateTxt = itemView.findViewById(R.id.message_date);
            messageTimeTxt = itemView.findViewById(R.id.message_time);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (progressBar != null){
            progressBar.setVisibility(View.GONE);
        }

        if (getItemCount()==0){
            emptyTextView.setVisibility(View.VISIBLE);
        }else {
            emptyTextView.setVisibility(View.GONE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
