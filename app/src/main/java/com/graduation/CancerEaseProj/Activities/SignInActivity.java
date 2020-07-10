package com.graduation.CancerEaseProj.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.Utils;

public class SignInActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: signIn";
    private ProgressDialog progressDialog;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeItems();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems() {
        Utils.forceRTLIfSupported(this);
        setTitle("تسجيل الدخول");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.msg_sign_in));
        progressDialog.setCancelable(false);

        Utils.checkStoragePermission(SignInActivity.this);
        Button signInBtn = findViewById(R.id.btn_sign_in);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginData ();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkLoginData () {

        EditText emailEdt = findViewById(R.id.record_no_edt);
        EditText passwordEdt = findViewById(R.id.password_edt);
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        progressDialog.show();

        if (email.isEmpty()){
            progressDialog.dismiss();
            emailEdt.requestFocus();
            emailEdt.setError(getString(R.string.enter_email_error));
        }else if (password.isEmpty()){
            progressDialog.dismiss();
            passwordEdt.requestFocus();
            passwordEdt.setError(getString(R.string.enter_password_error));
        }else if (!Utils.isNetworkAvailable(this)){
            progressDialog.dismiss();
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }else {
            Log.i(TAG,"signInWithEmailAndPassword");
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, " خطأ في تسجيل الدخول! " + "\n"
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
