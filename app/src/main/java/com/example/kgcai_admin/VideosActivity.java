package com.example.kgcai_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;


public class VideosActivity extends AppCompatActivity {

    FloatingActionsMenu btnAddVideo;
    FloatingActionButton btnFilipino, btnLanguage, btnReadings, btnNumeracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        btnAddVideo = findViewById(R.id.btnAddVideo);

        btnFilipino = findViewById(R.id.fab_filipino);
        btnLanguage = findViewById(R.id.fab_language_literacy);
        btnReadings = findViewById(R.id.fab_readings);
        btnNumeracy = findViewById(R.id.fab_numeracy);

        btnNumeracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVideosActivity.class);
                intent.putExtra("VideoFolder", "Numeracy_Videos");
                startActivity(intent);
            }
        });

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVideosActivity.class);
                intent.putExtra("VideoFolder", "LanguageLiteracy_Videos");
                startActivity(intent);
            }
        });

        btnFilipino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVideosActivity.class);
                intent.putExtra("VideoFolder", "Filipino_Videos");
                startActivity(intent);
            }
        });

        btnReadings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVideosActivity.class);
                intent.putExtra("VideoFolder", "Readings_Videos");
                startActivity(intent);
            }
        });
    }
}