package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

import java.util.regex.Pattern;

public class StudentRegisterActivity extends AppCompatActivity {

    private EditText txtName, txtEmail, txtPass;
    private Button btnRegister;

    FirebaseAuth firebaseAuth;

    String name, email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        txtName = findViewById(R.id.txtRegisterName);
        txtEmail = findViewById(R.id.txtRegisterEmail);
        txtPass = findViewById(R.id.txtRegisterPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = txtName.getText().toString().trim();
                email = txtEmail.getText().toString().trim();
                pass = txtPass.getText().toString().trim();

                if (name.isEmpty()) {
                    txtName.setError("Please Enter Student Name");
                    txtName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    txtEmail.setError("Please Enter Student Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmail.setError("Please Enter A Valid Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                if (pass.isEmpty()) {
                    txtPass.setError("Please Enter Student Password");
                    txtPass.requestFocus();
                    return;
                }
                if (pass.length() < 6) {
                    txtPass.setError("Password length should be more than 6 characters");
                    txtPass.requestFocus();
                    return;
                }

                registerStudent(email, pass);
            }

        });

    }

    private void registerStudent(String email, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel user = new UserModel(name, email);

                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user) //add it on firebase db
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


}


