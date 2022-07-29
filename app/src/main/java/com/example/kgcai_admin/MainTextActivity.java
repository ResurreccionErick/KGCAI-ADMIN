package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class MainTextActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton btnNumeracy, btnFilipino, btnLanguageLiteracy, btnReadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_text);

        toolbar = findViewById(R.id.toolbar_addVideos);

        btnNumeracy = findViewById(R.id.fab_numeracy);
        btnFilipino = findViewById(R.id.fab_filipino);
        btnReadings = findViewById(R.id.fab_readings);
        btnLanguageLiteracy = findViewById(R.id.fab_language_literacy);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Text");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnNumeracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTextActivity.class);
                intent.putExtra("VideoFolder", "Numeracy_Videos");
                startActivity(intent);
            }
        });

        btnLanguageLiteracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTextActivity.class);
                intent.putExtra("VideoFolder", "LanguageLiteracy_Videos");
                startActivity(intent);
            }
        });

        btnFilipino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTextActivity.class);
                intent.putExtra("VideoFolder", "Filipino_Videos");
                startActivity(intent);
            }
        });

        btnReadings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTextActivity.class);
                intent.putExtra("VideoFolder", "Readings_Videos");
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            MainTextActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}