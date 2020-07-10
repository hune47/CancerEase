package com.graduation.CancerEaseProj.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.Utils;

public class WelcomeActivity extends AppCompatActivity {
    public static final String TAG = "CancerEaseLog: Welcome";
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        checkAuth();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkAuth(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Log.i(TAG, "Fire base user: true");
                    Utils.getUserData(WelcomeActivity.this);
                }else {
                    Log.i(TAG, "Fire base user: false");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                }
            }
        };
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
