package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword, btnRegister;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new Dialog(LoginActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();

        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPass);
        btnLogin = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmail.setError("Please enter a valid email address");
                    txtEmail.requestFocus();
                    return;
                }
                if(email.isEmpty()||password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
                if(password.length() < 6){
                    txtPassword.setError("Please enter 6 digits password");
                    txtPassword.requestFocus();
                    return;
                }

                firebaseLogin(email,password);

            }
        });

        if(firebaseAuth.getCurrentUser()!=null){ //if user is currently logged in it will go to main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void firebaseLogin(String email, String password) {
        loadingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                     finish();
                 }else{
                     Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_SHORT).show();
                 }

                 loadingDialog.dismiss();
            }
        });
    }
}