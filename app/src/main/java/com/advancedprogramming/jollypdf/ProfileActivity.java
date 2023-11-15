package com.advancedprogramming.jollypdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profile_pic;
    private TextView profile_name,books_read,genre;
    private Button editProfile,deleteProfile;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_pic=findViewById(R.id.profile_pic);
        profile_name=findViewById(R.id.profile_name);
        books_read=findViewById(R.id.books_read);
        editProfile=findViewById(R.id.editProfile);
        deleteProfile=findViewById(R.id.deleteProfile);
        toolbar=findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Profile");
            actionBar.setSubtitle("JollyRead");
        }
        genre=findViewById(R.id.genre);
        String location= "/storage/emulated/0/JollyRead/UserData/curr.json";
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user=new User();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1=snapshot.getValue(User.class);
                Log.e("PDFErr", "onDataChange: "+user1.getName() );
                assert user1 != null;
                user.setName(user1.getName());
                user.setUserID(user1.getId());
                user.setEmail(user1.getEmail());
                user.setImageURL(user1.getImageURL());
                profile_name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("JSON", "onCancelled: "+error.getMessage() );
            }
        });
        StorageReference profilePicRef= FirebaseStorage.getInstance().getReference().child("profilepics").child(uid+".jpg");
        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this).load(uri).into(profile_pic);
        });
        UserJSON userJSON= new UserJSON();
        try {
            JSONHandler.readJSON(location,userJSON);
        } catch (Exception e) {
            Log.e("JSON", "onCreate: "+e.getMessage() );
            e.printStackTrace();
        }
        Log.e("PDFErr", "onCreate: "+user.getName() );

        int size=userJSON.getReadingHistory().size();
        books_read.setText("Books Read: "+String.valueOf(size));
        HashMap<String,Integer> genreMap=new HashMap<>();
        for(String name:userJSON.getReadingHistory()){
            String location2= "/storage/emulated/0/JollyRead/BookData/"+name+".json";
            BookJSON bookJSON=new BookJSON();
            try {
                JSONHandler.readJSON(location2,bookJSON);
            } catch (Exception e) {
                Log.e("JSON", "onCreate: "+e.getMessage() );
                e.printStackTrace();
            }
            String genre=bookJSON.getGenre();
            if(genreMap.containsKey(genre)){
                genreMap.put(genre,genreMap.get(genre)+1);
            }else{
                genreMap.put(genre,1);
            }
        }
        int max=0;
        String maxGenre="";
        for(String key:genreMap.keySet()){
            if(genreMap.get(key)>max){
                max=genreMap.get(key);
                maxGenre=key;
            }
        }
        genre.setText("Favourite Genre: "+maxGenre);
        editProfile.setOnClickListener(v -> {
            Intent i=new Intent(ProfileActivity.this,EditActivity.class);
            startActivity(i);
            finish();
        });
        deleteProfile.setOnClickListener(v -> {
            StorageReference profilePicRef2= FirebaseStorage.getInstance().getReference().child("profilepics").child(uid+".jpg");
            profilePicRef2.delete();
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).removeValue();
            FirebaseAuth.getInstance().getCurrentUser().delete();
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(ProfileActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.logout){
                File file=new File("/storage/emulated/0/JollyRead/UserData/curr.json");
                file.delete();
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }
}