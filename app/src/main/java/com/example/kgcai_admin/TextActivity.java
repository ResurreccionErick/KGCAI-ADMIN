package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class TextActivity extends AppCompatActivity {

    private RecyclerView textRv;
    private ProgressBar progressbar_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        textRv = findViewById(R.id.textRv);

        Toolbar toolbar = findViewById(R.id.toolbar_addVideos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Text");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            TextActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}