package com.advancedprogramming.jollypdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieBook);
        lottieAnimationView.animate();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase database=FirebaseDatabase.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user!=null){
                    String uid=mAuth.getCurrentUser().getUid();
                    database.getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            assert user != null;
                            Intent i=new Intent(SplashActivity.this,BrowseActivity.class);
                            new Intent(SplashActivity.this,ProfileActivity.class).putExtra("Extra_user",user);
                            i.putExtra("Extra_user",user);
                            startActivity(i);
                            finish();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Intent i=new Intent(SplashActivity.this,SignupActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2500);
    }
}



//package com.advancedprogramming.jollypdf;
//
//        import androidx.appcompat.app.AppCompatActivity;
//
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.os.Handler;
//
//        import com.airbnb.lottie.LottieAnimationView;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//
//public class SplashActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieBook);
//        lottieAnimationView.animate();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user!=null){
//                    Intent i = new Intent(SplashActivity.this, BrowseActivity.class);
//                    startActivity(i);
//                }else{
//                    Intent i=new Intent(SplashActivity.this,SignupActivity.class);
//                    startActivity(i);
//                }
//                finish();
//            }
//        }, 1500);
//    }
//}