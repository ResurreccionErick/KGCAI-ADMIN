package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kgcai_admin.adapter.ScoreAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.example.kgcai_admin.helper.ScoreDataModel;

public class LeaderboardsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView leaderboardRv;
    private ProgressBar progressBar;
    List<ScoreDataModel> list;

    ScoreAdapter scoreAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);

        toolbar = findViewById(R.id.toolbar_leaderboards);

        leaderboardRv = findViewById(R.id.leaderboardRv);
        progressBar = findViewById(R.id.leaderboardProgressboard);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Leaderboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Score");
        list = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        leaderboardRv.setLayoutManager(manager);
        leaderboardRv.setHasFixedSize(true);

        databaseReference.orderByChild("Numeracy_score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ScoreDataModel scoreDataModel = dataSnapshot.getValue(ScoreDataModel.class);
                    list.add(scoreDataModel);
                }
                scoreAdapter = new ScoreAdapter(list,LeaderboardsActivity.this);
                leaderboardRv.setAdapter(scoreAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            LeaderboardsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}