package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import com.example.kgcai_admin.helper.UserModel;

public class StudentRegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText txtFName,txtLName, txtEmail, txtPass;
    private Button btnRegister;
    private ImageView imgRegister;

    static int REQUEST_CODE = 1;

    Uri pickedImg;

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseFirestore firestore;

    String fName,lName,fullName, email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        toolbar = findViewById(R.id.toolbar_register);

        txtFName = findViewById(R.id.txtRegisterFName);
        txtLName = findViewById(R.id.txtRegisterLName);
        txtEmail = findViewById(R.id.txtRegisterEmail);
        txtPass = findViewById(R.id.txtRegisterPassword);
        imgRegister = findViewById(R.id.imgRegister);

        reference = FirebaseDatabase.getInstance().getReference().child("Score");

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgRegister.setOnClickListener(new View.OnClickListener() { //pick image
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestPermission();
                }else{
                    openGallery();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName = txtFName.getText().toString().trim();
                lName = txtLName.getText().toString().trim();
                fullName = fName + " " + lName;
                email = txtEmail.getText().toString().trim();
                pass = txtPass.getText().toString().trim();

                if(pickedImg==null){
                    Toast.makeText(getApplicationContext(), "Please pick a picture", Toast.LENGTH_SHORT).show();
                }
                else if (fName.isEmpty()) {
                    txtFName.setError("Please Enter Student First Name");
                    txtFName.requestFocus();
                    return;
                }
                else if (lName.isEmpty()) {
                    txtLName.setError("Please Enter Student Last Name");
                    txtLName.requestFocus();
                    return;
                }
                else if (email.isEmpty()) {
                    txtEmail.setError("Please Enter Student Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmail.setError("Please Enter A Valid Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                else if (pass.isEmpty()) {
                    txtPass.setError("Please Enter Student Password");
                    txtPass.requestFocus();
                    return;
                }
                else if (pass.length() < 6) {
                    txtPass.setError("Password length should be more than 6 characters");
                    txtPass.requestFocus();
                    return;
                }else{
                    registerStudent(email, pass);
                    registerStudentFirestore(email, pass);
                }
            }
        });

    }



    private void checkAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(StudentRegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image") , REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data!=null){
                pickedImg = data.getData();
                imgRegister.setImageURI(pickedImg); //set it into registerActivity imageview
            }

        }
    }

    private void registerStudentFirestore(String email, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DocumentReference reference = firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()); //this is for firestore
                            Map<String,Object> adminInfo = new HashMap<>();
                            adminInfo.put("fullName",fullName);
                            adminInfo.put("email", email);
                            adminInfo.put("isUser", "1");

                            reference.set(adminInfo);

                            UserModel user = new UserModel(fullName, email);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user) //add it on firebase db
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                            updateUi(fullName,pickedImg, firebaseAuth.getCurrentUser());
                                            finish();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerStudent(String email, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel user = new UserModel(fullName, email);

                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user) //add it on firebase db
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                            updateUi(fullName,pickedImg, firebaseAuth.getCurrentUser());
                                            finish();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(String name, Uri pickedImg, FirebaseUser currentUser) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_image");
        final StorageReference imgFilePath = storageReference.child(pickedImg.getLastPathSegment());

        imgFilePath.putFile(pickedImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(userProfileChangeRequest);
                        HashMap hashMap = new HashMap();
                        hashMap.put("name",name);
                        hashMap.put("image",uri.toString());
                        hashMap.put("score",0);
                        reference.child(currentUser.getUid()).setValue(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            StudentRegisterActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}


