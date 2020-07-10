package com.graduation.CancerEaseProj.Utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.graduation.CancerEaseProj.Activities.DoctorActivity;
import com.graduation.CancerEaseProj.Activities.PatientActivity;
import com.graduation.CancerEaseProj.Activities.SignInActivity;
import com.graduation.CancerEaseProj.Models.QResult;
import com.graduation.CancerEaseProj.Models.User;
import com.graduation.CancerEaseProj.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.graduation.CancerEaseProj.Utilities.Constants.CLOUD_USER_PHOTOS_DIR;
import static com.graduation.CancerEaseProj.Utilities.Constants.DEVICE_USER_PHOTOS_PATH;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;

public class Utils {
    private static final String TAG = "CancerEaseLog: Utils";
    private static final int REQUEST_WRITE_STORAGE = 112;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void checkStoragePermission(Activity activity) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void getUserData(final Activity activity){
        final SharedPref sharedPref = new SharedPref(activity);
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        String ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.i(TAG, "ID: " + ID);
        final DocumentReference doc = dbRef.collection(PATIENTS_COLLECTION).document(ID);
        final DocumentReference doctorDoc = dbRef.collection(DOCTORS_COLLECTION).document(ID);

        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Log.i(TAG, "Success: getUserData: Patient document is exists");
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                User.saveUserData(user, sharedPref, PATIENTS_COLLECTION);
                                patientTopicsRegistration(user.getRecordNumber());
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Log.i(TAG, "userId: " + userId);
                                if (!TextUtils.isEmpty(user.getPhoto())){
                                    Log.i(TAG, "getPhoto");
                                    createDirectoryAndSaveFile(activity, userId + ".jpg", PATIENTS_COLLECTION);
                                }else {
                                    openUserActivity(activity, PATIENTS_COLLECTION);
                                }
                            }
                        }else {
                            doctorDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        Log.i(TAG, "Success: getUserData: Doctor document is exists: " + documentSnapshot);
                                        User user = documentSnapshot.toObject(User.class);
                                        if (user != null) {
                                            User.saveUserData(user, sharedPref, DOCTORS_COLLECTION);
                                            doctorTopicsRegistration();
                                            if (!TextUtils.isEmpty(user.getPhoto())){
                                                createDirectoryAndSaveFile(activity, user.getPhoto(),DOCTORS_COLLECTION);
                                            }else {
                                                openUserActivity(activity, DOCTORS_COLLECTION);
                                            }
                                        }
                                    }else {
                                        Log.i(TAG, "Success: getUserData: Doctor document dose not exists");
                                        Toast.makeText(activity, R.string.document_does_not_exists, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "Exception: getUserData: " + e.getMessage());
                                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Exception: getUserData: " + e.getMessage());
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createDirectoryAndSaveFile (final Activity activity, final String imageName, final String type){
        boolean hasPermission = (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.i(TAG, "Has Permission: " + hasPermission);
        if (hasPermission) {
            File imgFile = new File(DEVICE_USER_PHOTOS_PATH + imageName);
            if (!imgFile.exists()) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child(CLOUD_USER_PHOTOS_DIR +"/"+ imageName);
                try {
                    final File localFile = File.createTempFile("Images", "jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            File directory = new File(DEVICE_USER_PHOTOS_PATH);
                            directory.mkdir();
                            File file = new File(DEVICE_USER_PHOTOS_PATH + imageName);
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                my_image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            openUserActivity(activity, type);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            openUserActivity(activity, type);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                openUserActivity(activity, type);
            }
        } else {
            openUserActivity(activity, type);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static void openUserActivity(Activity activity, String type){
        if (type.equals(PATIENTS_COLLECTION)){
            Intent intent = new Intent(activity, PatientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }else {
            Intent intent = new Intent(activity, DoctorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void forceRTLIfSupported(Activity activityِ) {
        activityِ.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void signOut(Activity activity){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(activity, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static  String dayName (int dayNo) {
        String dayStr;
        switch (dayNo){
            case 1:  dayStr= "الإثنين"; break;
            case 2:  dayStr= "الثلاثاء"; break;
            case 3:  dayStr= "الأربعاء"; break;
            case 4:  dayStr= "الخميس"; break;
            case 5:  dayStr= "االجمعة"; break;
            case 6:  dayStr= "السبت"; break;
            case 7:  dayStr= "الأحد"; break;
            default: dayStr = "";break;
        }
        return dayStr;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean compareTime(long comTime){
        long nowTime = new Date().getTime();
        if (comTime >= nowTime){
            return true;
        }else {
            return false;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static byte[] decodeUri(Context context, Uri uri, final int requiredSize, String imageName) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmapImage =  BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o2);

        File myPath = new File (DEVICE_USER_PHOTOS_PATH + imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();

        return b;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static void doctorTopicsRegistration(){
        CollectionReference patientsRef = FirebaseFirestore.getInstance().collection(PATIENTS_COLLECTION);
        String ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Query query = patientsRef.whereArrayContains("doctors", ID).orderBy("recordNumber",Query.Direction.ASCENDING);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size()>0){
                    for (int i = 0; i<queryDocumentSnapshots.size(); i++){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                        User user = documentSnapshot.toObject(User.class);
                        Log.i(TAG,"getRecordNumber: " + user.getRecordNumber());
                        FirebaseMessaging.getInstance().subscribeToTopic(user.getRecordNumber())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i(TAG,"task: " + task);
                                    }
                                });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"خطأ أثناء الوصول لبيانات المرضى!");
            }
        });

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static void patientTopicsRegistration(String recordNo){
        Log.i(TAG,"recordNo: " + recordNo);
        FirebaseMessaging.getInstance().subscribeToTopic(recordNo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG,"task: " + task);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"Exception: " + e.getMessage());
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        float factor = (float) Math.pow(10, places);
        value = value * factor;
        float tmp = Math.round(value);
        return  tmp / factor;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
