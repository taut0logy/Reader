package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieBook);
        lottieAnimationView.animate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent i = new Intent(SplashActivity.this, BrowseActivity.class);
                    startActivity(i);
                }else{
                    Intent i=new Intent(SplashActivity.this,SignupActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, 1500);
    }
}