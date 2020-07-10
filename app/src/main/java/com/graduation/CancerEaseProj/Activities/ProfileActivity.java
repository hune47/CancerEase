package com.graduation.CancerEaseProj.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.SharedPref;
import com.graduation.CancerEaseProj.Utilities.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import static com.graduation.CancerEaseProj.Utilities.Constants.CLOUD_USER_PHOTOS_DIR;
import static com.graduation.CancerEaseProj.Utilities.Constants.DEVICE_USER_PHOTOS_PATH;
import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;
import static com.graduation.CancerEaseProj.Utilities.Constants.PATIENTS_COLLECTION;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 1122;
    private static final int IMAGE_REQUEST_CODE = 11122;
    public static final String TAG = "CancerEaseLog: Profile";
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private TextView nameTxt, recordTxt, birthDate, genderTxt, nationality, patient_id, bloodType, mobile, email;
    private ImageView patientImage;
    private StorageReference storageRef;
    private CollectionReference userRef;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference imageStorage;
    private byte[] imgByts;
    private  String ID;
    private  String imageName;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeItems();
        getProfileData();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        sharedPref = new SharedPref(this);
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference(CLOUD_USER_PHOTOS_DIR);
        ID = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (sharedPref.getAccountType().equals(PATIENTS_COLLECTION)) {
            setTitle("     خدمات المرضى");
            userRef = dbRef.collection(PATIENTS_COLLECTION);
        }else {
            setTitle("     خدمات الطبيب");
            userRef = dbRef.collection(DOCTORS_COLLECTION);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.profile_updating));
        progressDialog.setCancelable(false);
        progressDialog.show();
        nameTxt = findViewById(R.id.patient_name);
        recordTxt = findViewById(R.id.patient_record_no);
        birthDate = findViewById(R.id.patient_birth_date);
        genderTxt = findViewById(R.id.patient_gender);
        nationality = findViewById(R.id.patient_nationality);
        patient_id = findViewById(R.id.patient_id);
        bloodType = findViewById(R.id.patient_blood_type);
        mobile = findViewById(R.id.patient_mobile);
        email = findViewById(R.id.patient_email);
        patientImage = findViewById(R.id.patient_image);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button updateImageBtn = findViewById(R.id.update_image_btn);
        updateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        Button saveProfileBtn = findViewById(R.id.save_profile_btn);
        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getProfileData(){
        nameTxt.setText(sharedPref.getYourName());
        recordTxt.setText(sharedPref.getYourRecordNumber());
        birthDate.setText(sharedPref.getYourBirthDate());
        genderTxt.setText(sharedPref.getYourGender());
        nationality.setText(sharedPref.getYourNationality());
        patient_id.setText(sharedPref.getYourIdNumber());
        bloodType.setText(sharedPref.getYourBloodType());
        mobile.setText(sharedPref.getYourMobile());
        email.setText(sharedPref.getYourEmail());
        checkPermissionForPhoto();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkPermissionForPhoto() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (hasPermission) {
            String imageName = sharedPref.getYourPhoto();
            File imgFile = new File(DEVICE_USER_PHOTOS_PATH + imageName);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                patientImage.setImageBitmap(myBitmap);
                progressDialog.dismiss();
            }else {
                progressDialog.dismiss();
                Toast.makeText(this, "لا يوجد صورة باسم " + imageName , Toast.LENGTH_SHORT).show();
            }
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "ليس لديك الإذن للوصول للصور!", Toast.LENGTH_SHORT).show();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void chooseImage() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == REQUEST_WRITE_STORAGE) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_WRITE_STORAGE);
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData()!=null) {
            imageUri = data.getData();
            patientImage.setImageURI(imageUri);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadImage(){
        if (getExtension(imageUri).equals("jpg")){
            progressDialog.show();
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            imageName = userID + ".jpg";
            imageStorage = storageRef.child(imageName);
            try {
               imgByts=  Utils.decodeUri(ProfileActivity.this, imageUri,100, imageName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "نوع الملف غير صالح!", Toast.LENGTH_SHORT).show();
        }

        uploadTask = imageStorage.putBytes(imgByts);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, "خطأ في رفع الصورة!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i(TAG, "iUri: " + uri);
                        sharedPref.setYourPhoto(""+imageName);
                        userRef.document(ID).update("photo",""+uri)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "فشل تحديث بيانات المستخدم", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
