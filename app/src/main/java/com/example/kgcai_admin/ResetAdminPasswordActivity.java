package com.example.kgcai_admin;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetAdminPasswordActivity extends AppCompatActivity {

    private TextInputEditText txtEmail;
    private Button btnResetPass;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_admin_password);

        Toolbar toolbar = findViewById(R.id.toolbar_resetPass);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.textEmailEditTxt);
        btnResetPass = findViewById(R.id.btnResetPass);

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmail.setError("Please enter a valid Email Address");
                    txtEmail.requestFocus();
                    return;
                }
                else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                }else{
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                    Toast.makeText(getApplicationContext(), "Check your email to reset your password", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
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
            ResetAdminPasswordActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}