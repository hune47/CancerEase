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
import com.graduation.CancerEaseProj.Models.Report;
import com.graduation.CancerEaseProj.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportsAdapter extends FirestoreRecyclerAdapter<Report, ReportsAdapter.MyHolder> {
    private OnClickListener onClickListener;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private Context context;
    private FirestoreRecyclerOptions<Report> options;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ReportsAdapter(@NonNull FirestoreRecyclerOptions<Report> options, ProgressBar progressBar, TextView emptyTextView, Context context) {
        super(options);
        this.options= options;
        this.progressBar = progressBar;
        this.emptyTextView = emptyTextView;
        this.context = context;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, final int position, @NonNull final Report model) {
        holder.reportTitleTxt.setText(model.getTitle());
        if (model.getTimestamp() != null){
            Date timestamp = model.getTimestamp().toDate();
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String reportDateStr = dateFormat.format(timestamp);
            holder.reportTimeTxt.setText(reportDateStr);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reports_list,parent,false);
        return new MyHolder(v);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class MyHolder extends RecyclerView.ViewHolder{
        TextView reportTitleTxt, reportTimeTxt;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            reportTitleTxt = itemView.findViewById(R.id.title_report);
            reportTimeTxt = itemView.findViewById(R.id.date_report);

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
        void onClick(Report report);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
