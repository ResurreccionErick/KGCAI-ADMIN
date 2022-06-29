package com.example.kgcai_admin;

import static com.example.kgcai_admin.AddVideosActivity.picked;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderVideo>{

    //context
    private Context context;

    //array list
    private ArrayList<ModelVideo> videoArrayList;

    //constructor

    public AdapterVideo(Context context, ArrayList<ModelVideo> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public HolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_video.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
        return new HolderVideo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVideo holder, int position) {
        // GET , FORMAT , SET DATA, HANDLE CLICKS etc...

        //get data
        ModelVideo modelVideo = videoArrayList.get(position);

        String id = modelVideo.getID();
        String title = modelVideo.getTitle();
        String timestamp = modelVideo.getTimeStamp();
        String videoUrl = modelVideo.getVideoUrl();

        //set data
        holder.titleTv.setText(title);
        //holder.timeTv.setText(formattedDateTime);
        setVideoUrl(modelVideo, holder);

        holder.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show alert dialog, confirm to delete
                android.app.AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Set")
                        .setMessage("Do you want to delete this video? "+title)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //confirm to delete
                                deleteVideo(modelVideo);
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.BLUE);
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,50,0);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);
            }
        });
    }

    private void setVideoUrl(ModelVideo modelVideo, HolderVideo holder) {
        //show progress bar
        holder.progressBar.setVisibility(View.VISIBLE);

        //get video url
        String videoUrl = modelVideo.getVideoUrl();

        //media controller for play, pause, seekbar, timer
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.videoView);

        Uri videoUri = Uri.parse(videoUrl);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.requestFocus();
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //video is ready to play

                mp.start();
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //to check if buffering , rendering, etc.
                switch (what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        //rendering started
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        //buffering started

                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;

                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        //buffering ended

                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                return false;
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start(); //restart the video if completed
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });




    }

    private void deleteVideo(ModelVideo modelVideo) {
        String videoId = modelVideo.getID();
        String videoUrl = modelVideo.getVideoUrl();

        //1. delete from firebase storage
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //successfully deleted from firebase storage

                        //2. delete from firebase database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(picked);
                        databaseReference.child(videoId).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context.getApplicationContext(), "Video was deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed deleting from firebase storage
                Toast.makeText(context.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size(); //return the size of the array list
    }

    //View holder class, holds, inits the UI views
    class HolderVideo extends RecyclerView.ViewHolder{

        //UI views of row_video.xml
        VideoView videoView;
        TextView titleTv;
        ProgressBar progressBar;
        FloatingActionButton fabDelete;

        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            //init UI views of row_video.xml
            videoView = itemView.findViewById(R.id.videoView);
            titleTv = itemView.findViewById(R.id.txtTitle);
            progressBar = itemView.findViewById(R.id.progressBar);
            fabDelete = itemView.findViewById(R.id.fab_delete);

        }
    }
}
