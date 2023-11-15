package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText etEditName,etEditMail,etEditPass,etEditConfirm,oldPass;
    private Button btnEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        profileImageView=findViewById(R.id.imageViewedit);
        etEditName=findViewById(R.id.eteditname);
        etEditMail=findViewById(R.id.eteditmail);
        etEditPass=findViewById(R.id.eteditpass);
        etEditConfirm=findViewById(R.id.eteditconfirm);
        oldPass=findViewById(R.id.oldpass);
        btnEdit=findViewById(R.id.btnedit);
        HashMap<String,Object> updates=new HashMap<>();
        btnEdit.setOnClickListener(v -> {
            String pass=oldPass.getText().toString();
            if(pass.isEmpty()) {
                oldPass.setError("Password is empty");
                return;
            }
            AuthCredential credential= EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(),pass);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if (!etEditName.getText().toString().isEmpty()) {
                        updates.put("name", etEditName.getText().toString());
                        UserJSON userJSON=new UserJSON();
                        userJSON.setUserID(etEditName.getText().toString());
                        String location= "/storage/emulated/0/JollyRead/UserData/curr.json";
                        try {
                            JSONHandler.writeJSON(location,userJSON);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!etEditMail.getText().toString().isEmpty()) {
                        FirebaseAuth.getInstance().getCurrentUser().updateEmail(etEditMail.getText().toString()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(EditActivity.this, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditActivity.this, "Email could not be updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (!etEditPass.getText().toString().isEmpty()) {
                        String pass1 = etEditPass.getText().toString();
                        String pass2 = etEditConfirm.getText().toString();
                        if (pass1.equals(pass2)) {
                            FirebaseAuth.getInstance().getCurrentUser().updatePassword(pass1).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(EditActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditActivity.this, "Password could not be updated.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (updates.size() > 0) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(updates).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(EditActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditActivity.this, ProfileActivity.class));
                                finish();
                            } else {
                                Toast.makeText(EditActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Intent i=new Intent(EditActivity.this,ProfileActivity.class);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(EditActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                    return;
                }
            });

        });
        profileImageView.setOnClickListener(v -> showPopup(v));
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
            updateDatabase(imageBitmap);
        } else if(requestCode==2 && resultCode==RESULT_OK) {
            Uri uri=data.getData();
            profileImageView.setImageURI(uri);
            updateDatabase(uri);
        }
    }
    private void updateDatabase(Bitmap imageBitmap) {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("profilepics").child(uid+".jpeg");
        storageReference.delete();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data=baos.toByteArray();
        storageReference.putBytes(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                storageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        String url=task1.getResult().toString();
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("profilepic").setValue(url).addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()) {
                                Toast.makeText(EditActivity.this, "Profile picture updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditActivity.this, "Profile picture could not be updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
    private void updateDatabase(Uri uri) {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("profilepics").child(uid+".jpg");
        storageReference.putFile(uri).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                storageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        String url=task1.getResult().toString();
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("profilepic").setValue(url).addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()) {
                                Toast.makeText(EditActivity.this, "Profile picture updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditActivity.this, "Profile picture could not be updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}