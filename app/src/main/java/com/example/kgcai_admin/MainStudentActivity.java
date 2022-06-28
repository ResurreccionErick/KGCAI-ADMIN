package com.example.kgcai_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainStudentActivity extends AppCompatActivity {

    private Button btnRegisterNewStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

        btnRegisterNewStudent = findViewById(R.id.btnRegisterNewStudent);

        btnRegisterNewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentRegisterActivity.class));
            }
        });
    }
}