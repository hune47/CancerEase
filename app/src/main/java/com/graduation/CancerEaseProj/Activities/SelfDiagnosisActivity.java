package com.graduation.CancerEaseProj.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.graduation.CancerEaseProj.Adapters.QuestionnaireAdapter;
import com.graduation.CancerEaseProj.Models.Answer;
import com.graduation.CancerEaseProj.Models.DoctorNotification;
import com.graduation.CancerEaseProj.Models.QResult;
import com.graduation.CancerEaseProj.Models.Question;
import com.graduation.CancerEaseProj.Models.Questionnaire;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.Models.UserNotification;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.AlertReceiver;
import com.graduation.CancerEaseProj.Utilities.DBHelper;
import com.graduation.CancerEaseProj.Utilities.MySingleton;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.FCM_API;
import static com.graduation.CancerEaseProj.Utilities.Constants.SERVER_KEY;

public class SelfDiagnosisActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: diagnos";
    public static final int MAX_Q_DAYS = 14;
    private Context context = this;
    private QuestionnaireAdapter questionnaireAdapter;
    private CollectionReference patientsRef;
    private SharedPref sharedPref;
    private ProgressBar progressBar;
    private List<Answer> answers;
    private int physicalTScale, psychologicalTScale, socialTScale, environmentTScale;
    private String ID, recommendations, NOTIFICATION_TITLE, NOTIFICATION_MESSAGE, TOPIC;
    private String contentType = "application/json";
    private DBHelper database;
    private RecyclerView recyclerView;
    private boolean rep_msg = false;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_diagnosis);
        initializeItems();
        if (checkPrevResults() >= MAX_Q_DAYS){
            loadQuestions();
        }else {
            Toast.makeText(context, getResources().getString(R.string.error_insert_time), Toast.LENGTH_SHORT).show();
            finish();
        }
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
        recyclerView = findViewById(R.id.recycler_view);
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        patientsRef = dbRef.collection(PATIENTS_COLLECTION);
        ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        progressBar = findViewById(R.id.progress_bar);
        database = new DBHelper(this);
        answers = new ArrayList<>();

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadQuestions(){
        List<Question> questionsList = database.getAllQuestions();
        for (Question question : questionsList){
            Answer answer = new Answer(1,question.getId(),question.getDomain_id());
            answers.add(answer);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionnaireAdapter = new QuestionnaireAdapter(this, questionsList, answers);
        recyclerView.setAdapter(questionnaireAdapter);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void calculateQuestionnaire(){
        progressBar.setVisibility(View.VISIBLE);
        for (Answer answer : answers){
            int answerNo = answer.getAnswer_no();
            int questionId = answer.getQuestion_id();
            int domainId = answer.getDomain_id();
            if((questionId == 3) || (questionId == 4) || (questionId == 26)){
                answerNo = 6 - answerNo;
            }
            if (domainId == 1){
                physicalTScale += answerNo;
            }else if (domainId == 2){
                psychologicalTScale += answerNo;
            }else if (domainId == 3){
                socialTScale += answerNo;
            }else if (domainId == 4) {
                environmentTScale += answerNo;
                Log.i(TAG, "environmentTScale = " + environmentTScale);
            }
        }
        Questionnaire questionnaire = new Questionnaire(physicalTScale, psychologicalTScale, socialTScale ,environmentTScale);
        saveQuestionnaire(questionnaire);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void saveQuestionnaire(Questionnaire questionnaire){
            patientsRef.document(ID).collection("questionnaire")
                    .add(questionnaire).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    progressBar.setVisibility(View.GONE);
                    Log.i(TAG, "add questionnaire: ok" );
                    detailsDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "خطأ في حفظ التقييم!", Toast.LENGTH_SHORT).show();
                }
            });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void detailsDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_questionnaire_details, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        TextView physicalTxt = mView.findViewById(R.id.physical_txt);
        TextView psychologicalTxt = mView.findViewById(R.id.psychological_txt);
        TextView socialTxt = mView.findViewById(R.id.social_txt);
        TextView environmentTxt = mView.findViewById(R.id.environment_txt);
        TextView recomTxt =  mView.findViewById(R.id.recom_text);

        ImageView physicalBar = mView.findViewById(R.id.physical_bar);
        ImageView psychologicalBar = mView.findViewById(R.id.psychological_bar);
        ImageView socialBar = mView.findViewById(R.id.social_bar);
        ImageView environmentBar = mView.findViewById(R.id.environment_bar);
        recommendations = "";
        result(physicalTScale, physicalTxt,physicalBar, 1);
        result(psychologicalTScale, psychologicalTxt,psychologicalBar, 2);
        result(socialTScale, socialTxt,socialBar, 3);
        result(environmentTScale, environmentTxt,environmentBar, 4);
        if (recommendations.isEmpty()){
            recommendations = getResources().getString(R.string.no_recommendations );
        }
        recomTxt.setText(recommendations);
        alert.setCancelable(false);

        alert.setNegativeButton("إغلاق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void result(int domTotal, TextView domTextView, ImageView bar,int domain){
        int level = 1;
        if (domain == 1){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal < 14 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                sendDoctorNotification(getResources().getString(R.string.go_to_emergency));
                recommendations += "- " +  getResources().getString(R.string.go_to_emergency ) + "\n";
            }else  if (domTotal < 28){
                level = 2;
                notificationSchedule(domain);
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);

            }else {
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "physical totalAverage: " + domTotal);
        }else if (domain == 2){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal < 12 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                sendDoctorNotification(getResources().getString(R.string.go_to_psychiatrist));
                recommendations += "- " +  getResources().getString(R.string.go_to_psychiatrist) + "\n";
            }else  if (domTotal < 24){
                level = 2;
                notificationSchedule(domain);
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);
            }else {
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "psychological totalAverage: " + domTotal);
        }else if (domain == 3){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal < 6 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                rep_msg = true;
                sendDoctorNotification(getResources().getString(R.string.go_to_hospital));
                recommendations += "- " +  getResources().getString(R.string.go_to_hospital) + "\n";
            }else  if (domTotal < 12){
                level = 2;
                notificationSchedule(domain);
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);
            }else{
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "social totalAverage: " + domTotal);
        }else if (domain == 4){
            domTextView.setText(String.valueOf(domTotal));
            if (domTotal <= 16 ){
                level = 1;
                domTextView.setTextColor(getResources().getColor(R.color.red));
                bar.setImageResource(R.drawable.red_shape);
                if (!rep_msg){
                    sendDoctorNotification(getResources().getString(R.string.go_to_hospital));
                    recommendations += "- " +  getResources().getString(R.string.go_to_hospital);
                }
            }else  if (domTotal < 32){
                level = 2;
                notificationSchedule(domain);
                domTextView.setTextColor(getResources().getColor(R.color.yellow));
                bar.setImageResource(R.drawable.yellow_shape);
            }else{
                level = 3;
                domTextView.setTextColor(getResources().getColor(R.color.green));
                bar.setImageResource(R.drawable.green_shape);
            }
            Log.i(TAG, "environment totalAverage: " + domTotal);
        }
        bar.getLayoutParams().height = domTotal*7;
        QResult qResult = new QResult();
        qResult.setDomain_id(domain);
        qResult.setLevel(level);
        qResult.setResult(domTotal);
        database.createResult(qResult);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void  notificationSchedule(int domain_id){
        Calendar notifyCalendar = Calendar.getInstance();
        int dom  = notifyCalendar.get(Calendar.DAY_OF_MONTH);
        int counter = 0;
        List<UserNotification> notifications =  database.getDomainNotifications(domain_id,2);
        for (UserNotification notification : notifications){
            notifyCalendar.set(Calendar.DAY_OF_MONTH, dom + counter);
            int A_KEY = notification.getId()+(domain_id*10);
            String message = notification.getNotification();
            String url = notification.getUrl();
            String video = notification.getVideo();
            setNotification(notifyCalendar, message, url, video, A_KEY);
            counter++;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setNotification(Calendar notifyCalendar, String message, String url, String video, int A_KEY){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title","العناية بالصحة");
        intent.putExtra("message",message);

        if (url != null)
            if (!url.isEmpty())
                intent.putExtra("url", url);

        if (video != null)
            if (!video.isEmpty())
                intent.putExtra("video", video);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, A_KEY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyCalendar.getTimeInMillis(), pendingIntent);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int checkPrevResults(){
        int difDays = 0;
        QResult qResult = database.getLastResult();
        if (qResult != null){
            Date qDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                qDate = format.parse(qResult.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar today = Calendar.getInstance();
            Calendar doq = Calendar.getInstance();
            doq.setTime(qDate);
            difDays = today.get(Calendar.DAY_OF_YEAR) - doq.get(Calendar.DAY_OF_YEAR);
            return difDays;
        }else {
            return MAX_Q_DAYS;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_menu_btn) {
            calculateQuestionnaire();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendDoctorNotification(String message){
        TOPIC = "/topics/" + sharedPref.getYourRecordNumber(); //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = sharedPref.getYourName();
        NOTIFICATION_MESSAGE = message;

        final DoctorNotification doctorNotification = new DoctorNotification();
        doctorNotification.setTitle(sharedPref.getYourName());
        doctorNotification.setMessage(message);
        doctorNotification.setEmail(sharedPref.getYourEmail());
        doctorNotification.setRecordNo(sharedPref.getYourRecordNumber());
        doctorNotification.setStatus(1);

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        doctorSaveNotifications(doctorNotification);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "VolleyError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "VolleyError:" + error.getMessage());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", SERVER_KEY);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static void doctorSaveNotifications(final DoctorNotification doctorNotification){
        final CollectionReference doctorsRef = FirebaseFirestore.getInstance().collection(DOCTORS_COLLECTION);
        final String ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.i(TAG,"ID: " + ID);
        Log.i(TAG,"doctorsRef: " + doctorsRef);
        Query query = doctorsRef.whereArrayContains("patients", ID);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i(TAG,"onSuccess" );
                if (queryDocumentSnapshots.size()>0){
                    for (int i = 0; i<queryDocumentSnapshots.size(); i++){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                        User user = documentSnapshot.toObject(User.class);
                        doctorsRef.document(user.getEmail())
                                .collection("notifications")
                                .add(doctorNotification);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"خطأ أثناء الوصول لبيانات الطبيب! :" + e.getMessage());
            }
        });

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
