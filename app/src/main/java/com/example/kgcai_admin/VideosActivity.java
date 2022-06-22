package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Member;

public class VideosActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private Button btnSelectVid, btnUploadVid;
    private EditText txtVideoName;
    private VideoView videoView;
    private Uri videoUri;
    MediaController mediaController;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        btnSelectVid = findViewById(R.id.btn_choose_vid_add);
        btnUploadVid = findViewById(R.id.btn_upload_vid_add);
        txtVideoName = findViewById(R.id.txt_video_name_add);
        videoView = findViewById(R.id.video_view_add);

        progressBar = findViewById(R.id.progress_bar_video);

        mediaController = new MediaController(this);

        storageReference = FirebaseStorage.getInstance().getReference("videos1");
        databaseReference = FirebaseDatabase.getInstance().getReference("videos2");

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        btnSelectVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();

                btnUploadVid.setVisibility(View.VISIBLE);

            }
        });

        btnUploadVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();

            }
        });

    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null);
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);

        }
        private String getFileExt(Uri videoUri){
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(videoUri));
        }

    private void uploadVideo() {
        progressBar.setVisibility(View.VISIBLE);

        if(videoUri != null){
            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(videoUri));

            reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();
                    VideoModel model = new VideoModel(txtVideoName.getText().toString().trim(),
                            taskSnapshot.getUploadSessionUri().toString());

                    String upload = databaseReference.push().getKey();
                    databaseReference.child(upload).setValue(model);

                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}