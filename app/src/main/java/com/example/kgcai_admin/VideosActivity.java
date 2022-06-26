package com.example.kgcai_admin;

import static com.example.kgcai_admin.AddVideosActivity.picked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class VideosActivity extends AppCompatActivity {

    FloatingActionButton btnAddVideo;

    //arraylist of model video
    private ArrayList<ModelVideo> videoArrayList;

    //adapter
    private AdapterVideo adapterVideo;

    private RecyclerView videosRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        videosRv = findViewById(R.id.videoRv);

        btnAddVideo = findViewById(R.id.btnAddVideo);

        loadVideosFromFirebase();

        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVideosActivity.class);
                intent.putExtra("VideoFolder", picked);
                startActivity(intent);
            }
        });
    }

    private void loadVideosFromFirebase() {
        //get the clicked subj intent from MainVideosActivity
        Bundle extras = getIntent().getExtras();
        picked = extras.getString("VideoFolder");

        //init arraylist
        videoArrayList = new ArrayList<>();

        //db reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(picked);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelVideo modelVideo = ds.getValue(ModelVideo.class);

                    //add model/data into list
                    videoArrayList.add(modelVideo);
                }
                //set up adapter
                adapterVideo = new AdapterVideo(VideosActivity.this, videoArrayList);

                //set adapter to recyclerview
                videosRv.setAdapter(adapterVideo);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}