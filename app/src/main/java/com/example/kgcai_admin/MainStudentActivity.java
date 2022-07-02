package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainStudentActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button btnRegisterNewStudent,btnLeaderboards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

        toolbar = findViewById(R.id.toolbar_mainStudent);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnLeaderboards = findViewById(R.id.btnLeaderboards);
        btnRegisterNewStudent = findViewById(R.id.btnRegisterNewStudent);

        btnRegisterNewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentRegisterActivity.class));
            }
        });

        btnLeaderboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LeaderboardsActivity.class));
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            MainStudentActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}