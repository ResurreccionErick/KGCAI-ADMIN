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

import com.example.kgcai_admin.helper.UserModel;
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

public class AdminRegistrationActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText txtFName,txtLName, txtEmail, txtPass;
    private Button btnRegister;

    static int REQUEST_CODE = 1;

    Uri pickedImg;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    DatabaseReference reference;
    DatabaseReference adminReference;

    String fName,lName,fullName, email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        toolbar = findViewById(R.id.toolbar_register);

        txtFName = findViewById(R.id.txtRegisterAdminFName);
        txtLName = findViewById(R.id.txtRegisterAdminLName);
        txtEmail = findViewById(R.id.txtRegisterAdminEmail);
        txtPass = findViewById(R.id.txtRegisterAdminPassword);


        reference = FirebaseDatabase.getInstance().getReference().child("Score");
        adminReference = FirebaseDatabase.getInstance().getReference().child("AdminData");
        firestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnAdminRegister);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName = txtFName.getText().toString().trim();
                lName = txtLName.getText().toString().trim();
                fullName = fName + " " + lName;
                email = txtEmail.getText().toString().trim();
                pass = txtPass.getText().toString().trim();

                if (fName.isEmpty()) {
                    txtFName.setError("Please Enter Admin First Name");
                    txtFName.requestFocus();
                    return;
                }
                else if (lName.isEmpty()) {
                    txtLName.setError("Please Enter Admin Last Name");
                    txtLName.requestFocus();
                    return;
                }
                else if (email.isEmpty()) {
                    txtEmail.setError("Please Enter Admin Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmail.setError("Please Enter A Valid Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                else if (pass.isEmpty()) {
                    txtPass.setError("Please Enter Admin Password");
                    txtPass.requestFocus();
                    return;
                }
                else if (pass.length() < 6) {
                    txtPass.setError("Password length should be more than 6 characters");
                    txtPass.requestFocus();
                    return;
                }else{

                    registerAdminFirestore(email, pass);
                }
            }
        });

    }

    private void registerAdminFirestore(String email, String pass) {
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DocumentReference reference = firestore.collection("Users").document(firebaseAuth.getCurrentUser().getDisplayName()); //this is for firestore
                                Map<String,Object> adminInfo = new HashMap<>();
                                adminInfo.put("fullName",fullName);
                                adminInfo.put("email", email);
                                adminInfo.put("isAdmin", "1");

                                reference.set(adminInfo);

                                UserModel user = new UserModel(fullName, email);
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(user) //add it on firebase db
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
//            AdminRegistrationActivity.this.finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}

