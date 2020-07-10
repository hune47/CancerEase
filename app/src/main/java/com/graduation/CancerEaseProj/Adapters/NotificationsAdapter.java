package com.graduation.CancerEaseProj.Adapters;

import android.content.Context;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.graduation.CancerEaseProj.Models.DoctorNotification;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class NotificationsAdapter extends FirestoreRecyclerAdapter<DoctorNotification, NotificationsAdapter.MyHolder> {
    private OnClickListener onClickListener;
    private Context context;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private FirestoreRecyclerOptions<DoctorNotification> options;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public NotificationsAdapter(@NonNull FirestoreRecyclerOptions<DoctorNotification> options, ProgressBar progressBar, TextView emptyTextView, Context context) {
        super(options);
        this.options = options;
        this.context = context;
        this.progressBar = progressBar;
        this.emptyTextView = emptyTextView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onBindViewHolder(@NonNull final MyHolder holder, final int position, @NonNull final DoctorNotification model) {
        holder.recordNumberTxt.setText(model.getRecordNo());
        holder.nameTxt.setText(model.getTitle());
        if (model.getStatus() == 1) {
            holder.statusImg.setBackgroundColor(context.getResources().getColor(R.color.red));
        }else if (model.getStatus() == 2) {
            holder.statusImg.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }else if (model.getStatus() == 3) {
            holder.statusImg.setBackgroundColor(context.getResources().getColor(R.color.green));
        }


        DocumentReference patientRef =  FirebaseFirestore.getInstance()
                .collection(PATIENTS_COLLECTION).document(model.getEmail());
        patientRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        if (!TextUtils.isEmpty(user.getPhoto())){
                            String url = user.getPhoto();
                            Picasso.get().load(url).into(holder.chatImage);
                        }
                    }
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_list,parent,false);
        return new MyHolder(v);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class MyHolder extends RecyclerView.ViewHolder{
        TextView recordNumberTxt, nameTxt;
        ImageView statusImg;
        CircleImageView chatImage;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            recordNumberTxt = itemView.findViewById(R.id.record_no);
            nameTxt = itemView.findViewById(R.id.patient_name);
            statusImg = itemView.findViewById(R.id.patient_status);
            chatImage = itemView.findViewById(R.id.profile_image);
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
        void onClick(DoctorNotification doctorNotification);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
