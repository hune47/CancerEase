package com.graduation.CancerEaseProj.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.graduation.CancerEaseProj.Models.Appointment;
import com.graduation.CancerEaseProj.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentsAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentsAdapter.MyHolder> {
    private OnClickListener onClickListener;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private Context context;
    private FirestoreRecyclerOptions<Appointment> options;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public AppointmentsAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options, ProgressBar progressBar, TextView emptyTextView, Context context) {
        super(options);
        this.options= options;
        this.progressBar = progressBar;
        this.emptyTextView = emptyTextView;
        this.context = context;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, final int position, @NonNull final Appointment model) {
        holder.clinicTxt.setText(model.getClinic());
        String isConfirmed = "";
        if (model.getConfirmed() == 1){
            isConfirmed = context.getString(R.string.confirmed_appointment);
            holder.appointTitleTxt.setText(isConfirmed);
            holder.appointTitleTxt.setTextColor(context.getResources().getColor(R.color.green));
        }else {
            isConfirmed = context.getString(R.string.waiting_appointment);
            holder.appointTitleTxt.setText(isConfirmed);
            holder.appointTitleTxt.setTextColor(context.getResources().getColor(R.color.red));
        }
        if (model.getTimestamp() != null){
            Date messageTime = model.getTimestamp().toDate();
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            DateFormat timeFormat = SimpleDateFormat.getTimeInstance( DateFormat.SHORT,Locale.US);
            String messageDateStr = dateFormat.format(messageTime);
            String messageTimeStr = timeFormat.format(messageTime);
            holder.appointDateTxt.setText(messageDateStr);
            holder.appointTimeTxt.setText(messageTimeStr);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointments_list,parent,false);
        return new MyHolder(v);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class MyHolder extends RecyclerView.ViewHolder{
        TextView clinicTxt, appointTitleTxt, appointTimeTxt, appointDateTxt;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            clinicTxt = itemView.findViewById(R.id.clinic_txt);
            appointTitleTxt = itemView.findViewById(R.id.appoint_title_txt);
            appointTimeTxt = itemView.findViewById(R.id.appoint_time_txt);
            appointDateTxt = itemView.findViewById(R.id.appoint_date_txt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onClickListener != null && position != RecyclerView.NO_POSITION){
                        onClickListener.onClick(options.getSnapshots().get(position));
                    }
                }
            });
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
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnClickListener {
        void onClick(Appointment appointment);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
