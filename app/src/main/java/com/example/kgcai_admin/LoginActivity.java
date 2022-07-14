package com.example.kgcai_admin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private LinearLayout txtRegister;
    private TextView txtForgotPassAdmin;
    private Button btnLogin;
    private Dialog loadingDialog;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    DatabaseReference adminReference;


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
        txtForgotPassAdmin = findViewById(R.id.btnForgotPass);
        txtRegister = findViewById(R.id.btnRegisterNewAdmin);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminRegistrationActivity.class));
            }
        });

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


    }

    private void firebaseLogin(String email, String password) {
        loadingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(firebaseAuth.getCurrentUser()!=null){ //if user is currently logged in it will go to main activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                checkIfAdmin(firebaseAuth.getCurrentUser().getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_SHORT).show();
            }
        });
        loadingDialog.dismiss();
    }

    private void checkIfAdmin(String uid) {
        DocumentReference reference = firestore.collection("Users").document(uid);

        //extract data from document
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                //identify the user if its admin or not
                if(documentSnapshot.getString("isAdmin")!=null){
                    //user is admin
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    LoginActivity.this.finish();
                }else{
                    firebaseAuth.signOut();
                    Toast.makeText(getApplicationContext(), "Sorry, you are not an admin.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    System.exit(0);
                }
            }
        });
    }
}