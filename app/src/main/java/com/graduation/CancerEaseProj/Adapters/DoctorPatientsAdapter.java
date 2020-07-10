package com.graduation.CancerEaseProj.Adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class DoctorPatientsAdapter extends FirestoreRecyclerAdapter<User, DoctorPatientsAdapter.MyHolder> {
    private OnClickListener onClickListener;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private FirestoreRecyclerOptions<User> options;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public DoctorPatientsAdapter(@NonNull FirestoreRecyclerOptions<User> options, ProgressBar progressBar, TextView emptyTextView) {
        super(options);
        this.options= options;
        this.progressBar = progressBar;
        this.emptyTextView = emptyTextView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onBindViewHolder(@NonNull final MyHolder holder, int position, @NonNull User model) {
        holder.recordNumberTxt.setText(model.getRecordNumber());
        holder.nameTxt.setText(model.getName());
        holder.genderTxt.setText(model.getGender());
        String age = getAge(model.getBirthDate());
        holder.ageTxt.setText(age);

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patients_list,parent,false);
        return new MyHolder(v);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class MyHolder extends RecyclerView.ViewHolder{
        TextView recordNumberTxt, nameTxt, ageTxt, genderTxt;
        CircleImageView chatImage;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            recordNumberTxt = itemView.findViewById(R.id.record_no);
            nameTxt = itemView.findViewById(R.id.patient_name);
            ageTxt = itemView.findViewById(R.id.patient_age);
            genderTxt = itemView.findViewById(R.id.patient_gender);
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
    private String getAge(String myDob){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        try {
            date = format.parse(myDob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar dob = Calendar.getInstance(); //15-08-1994
        Calendar today = Calendar.getInstance(); // 23/2/2020
        dob.setTime(date);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnClickListener {
        void onClick(User user);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
