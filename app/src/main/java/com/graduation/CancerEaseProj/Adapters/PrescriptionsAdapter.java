package com.graduation.CancerEaseProj.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.graduation.CancerEaseProj.Models.Prescription;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.Utils;

import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class PrescriptionsAdapter extends FirestoreRecyclerAdapter<Prescription, PrescriptionsAdapter.MyHolder> {
    private OnClickListener onClickListener;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private FirestoreRecyclerOptions<Prescription> options;
    private String type;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public PrescriptionsAdapter(@NonNull FirestoreRecyclerOptions<Prescription> options,
                                ProgressBar progressBar, TextView emptyTextView, String type) {
        super(options);
        this.options= options;
        this.progressBar = progressBar;
        this.emptyTextView = emptyTextView;
        this.type = type;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Prescription model) {
        holder.presNameTxt.setText(model.getPres_name());
        holder.dosageTxt.setText(model.getDosage());
        if (Utils.compareTime(model.getAlarm())|| model.getEd_alarm() == 1 ) {
            holder.alarmBtn.setImageResource(R.drawable.ic_bell);
        }else {
            holder.alarmBtn.setImageResource(R.drawable.ic_bell_gray);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription,parent,false);
        return new MyHolder(v);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class MyHolder extends RecyclerView.ViewHolder{
        TextView presNameTxt, dosageTxt;
        ImageView alarmBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            presNameTxt = itemView.findViewById(R.id.pres_name_txt);
            dosageTxt = itemView.findViewById(R.id.dosage_txt);
            alarmBtn = itemView.findViewById(R.id.alarm_btn);

            if (type.equals(PATIENTS_COLLECTION)){
                alarmBtn.setOnClickListener(new View.OnClickListener() {
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
        void onClick(Prescription prescription);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
