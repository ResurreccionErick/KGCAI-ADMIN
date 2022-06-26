package com.example.kgcai_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class MainVideosActivity extends AppCompatActivity {

    private FloatingActionButton btnNumeracy, btnLanguageLiteracy, btnFilipino, btnReadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_videos);

        btnNumeracy = findViewById(R.id.fab_numeracy);
        btnFilipino = findViewById(R.id.fab_filipino);
        btnReadings = findViewById(R.id.fab_readings);
        btnLanguageLiteracy = findViewById(R.id.fab_language_literacy);


        btnNumeracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideosActivity.class);
                intent.putExtra("VideoFolder", "Numeracy_Videos");
                startActivity(intent);
            }
        });

        btnLanguageLiteracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideosActivity.class);
                intent.putExtra("VideoFolder", "LanguageLiteracy_Videos");
                startActivity(intent);
            }
        });

        btnFilipino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideosActivity.class);
                intent.putExtra("VideoFolder", "Filipino_Videos");
                startActivity(intent);
            }
        });

        btnReadings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideosActivity.class);
                intent.putExtra("VideoFolder", "Readings_Videos");
                startActivity(intent);
            }
        });

    }


}