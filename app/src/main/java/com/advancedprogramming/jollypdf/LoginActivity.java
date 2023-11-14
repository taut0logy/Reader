package com.advancedprogramming.jollypdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText etEmail=findViewById(R.id.etloginname);
        EditText etPassword=findViewById(R.id.etloginpass);
        Button btnLogin=findViewById(R.id.btnsignup);
        TextView dont=findViewById(R.id.tvdont);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        btnLogin.setOnClickListener(v-> {
            String email= etEmail.getText().toString();
            String password= etPassword.getText().toString();
            if(email.isEmpty()) {
                etEmail.setError("Email is required.");
                return;
            }
            if(password.isEmpty()) {
                etPassword.setError("Password is required.");
                return;
            }
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(this, "User logged in successfully.", Toast.LENGTH_SHORT).show();
                    if(mAuth.getCurrentUser()!=null) {
                        String uid=mAuth.getCurrentUser().getUid();
                        database.getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user=snapshot.getValue(User.class);
                                assert user != null;
                                Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                new Intent(LoginActivity.this,ProfileActivity.class).putExtra("Extra_user",user);
                                i.putExtra("Extra_user",user);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
                else {
                    //Toast.makeText(this, "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Exception e=task.getException();
                    Log.e("MyError",e.getMessage());
                    if(e==null) {
                        Toast.makeText(this, "Error: Unknown error occurred.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(e.getMessage().contains("password")) {
                        etPassword.setError("Invalid password.");
                        return;
                    }
                    if(e.getMessage().contains("email")) {
                        etEmail.setError("Invalid email.");
                        return;
                    }
                    if(e.getMessage().contains("no user record")) {
                        etEmail.setError("User does not exist.");
                        return;
                    }
                    if(e.getMessage().contains("network error")) {
                        Toast.makeText(this, "Error: Network error occurred.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(e.getMessage().contains("INVALID_LOGIN_CREDENTIALS")) {
                        Toast.makeText(this, "Error: Invalid login credentials.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(e.getMessage().contains("blocked")) {
                        Toast.makeText(this, "Error: Too many requests. Try again later.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dont.setOnClickListener(v-> {
            Intent i=new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(i);
            finish();
        });
    }

}