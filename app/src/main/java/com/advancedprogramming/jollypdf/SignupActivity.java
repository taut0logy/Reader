package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.graphics.Bitmap;
/*import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;*/
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class SignupActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    private ImageView profileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        EditText etName=findViewById(R.id.etsignupname);
        EditText etEmail=findViewById(R.id.etsignupmail);
        EditText etPassword=findViewById(R.id.etsignuppass);
        EditText etConfirmPassword=findViewById(R.id.etsignuppassconf);
        Button btnSignup=findViewById(R.id.btnsignup);
        TextView dont=findViewById(R.id.tvdont);
        profileImageView=findViewById(R.id.imageView);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        profileImageView.setOnClickListener(v -> showPopup(v));
        btnSignup.setOnClickListener(v-> {
            String name= etName.getText().toString();
            String email= etEmail.getText().toString();
            String password= etPassword.getText().toString();
            String confirmPassword= etConfirmPassword.getText().toString();
            if(name.isEmpty()) {
                etName.setError("Name is required.");
                return;
            }
            if(email.isEmpty()) {
                etEmail.setError("Email is required.");
                return;
            }
            if(password.isEmpty()) {
                etPassword.setError("Password is required.");
                return;
            }
            if(confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Confirm Password is required.");
                return;
            }
            if(!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Password and Confirm Password must be same.");
                return;
            }
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(this, "User created successfully.", Toast.LENGTH_SHORT).show();
                    if(mAuth.getCurrentUser()!=null) {
                        String uid=mAuth.getCurrentUser().getUid();
                        StorageReference profilePicRef=storage.getReference().child("profilepics").child(uid+".jpg");
                        //get image from imageview
                        Bitmap bitmap=((BitmapDrawable)profileImageView.getDrawable()).getBitmap();
                        //compress image
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        byte[] data=baos.toByteArray();
                        //upload image
                        profilePicRef.putBytes(data).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                Toast.makeText(this, "Profile pic uploaded successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        String url=profilePicRef.getDownloadUrl().toString();
                        User user=new User(uid,name,email,url);
                        UserJSON userJSON=new UserJSON();
                        userJSON.setUserID(name);
                        try {
                            JSONHandler.writeJSON("/storage/emulated/0/JollyRead/UserData/curr.json",userJSON);
                        } catch (Exception e) {
                            Log.e("PDFError",e.getMessage());
                            e.printStackTrace();
                        }
                        database.getReference().child("users").child(uid).setValue(user).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                Toast.makeText(this, "User data saved successfully.", Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(SignupActivity.this,BrowseActivity.class);
                                Intent i2=new Intent(SignupActivity.this,ProfileActivity.class);
                                new Intent(SignupActivity.this,BrowseActivity.class).putExtra("Extra_usernamw",name);
                                i2.putExtra("Extra_user",user);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else {
                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dont.setOnClickListener(v-> {
            Intent i=new Intent(SignupActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        });


    }
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.profilepic_popup,popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
                if(item.getItemId()==R.id.takepic) {
                    openCamera();
                    return true;
                } else if(item.getItemId()==R.id.choosepic) {
                    selectFromGallery();
                    return true;
                }
            Toast.makeText(this, "No Camera App Found.", Toast.LENGTH_SHORT).show();
            return false;
        });
        popup.show();
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(takePictureIntent, 1);
        } else {
            Toast.makeText(this, "No camera app found on your device.", Toast.LENGTH_SHORT).show();
        }
    }
    private void selectFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImageView.setImageBitmap(imageBitmap);
        } else if(requestCode==2 && resultCode==RESULT_OK) {
            Uri uri=data.getData();
            profileImageView.setImageURI(uri);
        }
    }

    /*private void cropImage(Uri uri) {
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("return-data",true);
        startActivityForResult(intent,REQUEST_IMAGE_CROP);
    }
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        Bitmap circleBitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        BitmapShader shader=new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint=new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas canvas=new Canvas(circleBitmap);
        float radius=width/2f;
        canvas.drawCircle(radius,radius,radius,paint);
        return circleBitmap;
    }*/


    private File createImageFile() throws IOException {
        // Create an image file name
        String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JOLLYREAD_" + id + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}