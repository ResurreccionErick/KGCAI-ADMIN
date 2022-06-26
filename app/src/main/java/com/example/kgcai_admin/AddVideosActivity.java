package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddVideosActivity extends AppCompatActivity {

    private EditText txtTitle;
    private VideoView videoView;
    private Button btnUploadVid;
    private FloatingActionButton btnPickVideo;

    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermission;

    private Uri videoUri = null; //Uri picked video

    private String title;
    public static String picked;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        txtTitle = findViewById(R.id.txtVideoTitle);
        videoView = findViewById(R.id.videoView);
        btnUploadVid = findViewById(R.id.btnUploadVid);
        btnPickVideo = findViewById(R.id.btnPickVideo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading video");
        progressDialog.setCanceledOnTouchOutside(false);

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //upload video
        btnUploadVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = txtTitle.getText().toString();

                if(title.isEmpty()){
                    txtTitle.setError("Please enter title of the video");
                    txtTitle.requestFocus();
                    return;
                }else if (videoUri==null){
                    Toast.makeText(getApplicationContext(), "Please pick a video to upload", Toast.LENGTH_SHORT).show();
                }else{
                    uploadVideoFirebase();
                }
            }
        });

        //pick video from camera or gallery
        btnPickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPickDialog();
            }
        });
    }

    private void uploadVideoFirebase() {
        progressDialog.show();

        Bundle extras = getIntent().getExtras();
        picked = extras.getString("VideoFolder");


        //timestamp
        String timeStamp = ""+System.currentTimeMillis();

        String filePathAndName = picked+"/"+"video_"+timeStamp;

        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

        //upload video, you can upload any type of file using this method
        storageReference.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //video uploaded, get url of uploaded video
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if(uriTask.isSuccessful()){
                            //url of uploaded video is received

                            //now we can add video details to our firebase db
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id",""+timeStamp);
                            hashMap.put("title",""+"Title: "+title);
                            hashMap.put("timestamp",""+timeStamp);
                            hashMap.put("videoUrl",""+downloadUri);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(picked);
                            reference.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //video details was added into db
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Video uploaded", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainVideosActivity.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void videoPickDialog() {
        //options to display
        String[] options = {"Camera", "Gallery"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i==0){
                    //when option camera was chosen
                    if(!checkCameraPermission()){
                        //camera permission not allowed, request it
                        requestCameraPermission();
                    }else{
                        //permission already allowed, take picture
                        videoPickCamera();
                    }

                }else if(i==1){
                    //when option gallery was choosen
                    VideoPickGallery();
                }
            }
        }).show();
    }

    private void requestCameraPermission(){
        //request camera permission
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);

    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }


    private void VideoPickGallery(){

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos") , VIDEO_PICK_GALLERY_CODE);


    }

    private void videoPickCamera(){
        //pick video from camera intent
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }


    private void setVideoToView() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //set media controller to video view
        videoView.setMediaController(mediaController);

        //set  video uri
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.pause();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    //check permission allowed or not
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean galleryAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && galleryAccepted) {
                        //both permission accepted
                        videoPickCamera();
                    }
                    else{
                        //both or one permission denied
                        Toast.makeText(getApplicationContext(), "Camera and Storage are required, You need to re-install the application", Toast.LENGTH_SHORT).show();
                        requestCameraPermission();

                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == VIDEO_PICK_GALLERY_CODE){
                videoUri = data.getData();

                //showing the picked video in videoView
                setVideoToView();
            }else if (requestCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data.getData();

                //showing the picked video in videoView
                setVideoToView();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}