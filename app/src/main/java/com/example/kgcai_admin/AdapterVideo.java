package com.example.kgcai_admin;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        //format timestamp
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(timestamp));
//        String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();

        //set data
        holder.titleTv.setText(title);
        //holder.timeTv.setText(formattedDateTime);
        setVideoUrl(modelVideo, holder);
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

    @Override
    public int getItemCount() {
        return videoArrayList.size(); //return the size of the array list
    }

    //View holder class, holds, inits the UI views
    class HolderVideo extends RecyclerView.ViewHolder{

        //UI views of row_video.xml
        VideoView videoView;
        TextView titleTv, timeTv;
        ProgressBar progressBar;

        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            //init UI views of row_video.xml
            videoView = itemView.findViewById(R.id.videoView);
            titleTv = itemView.findViewById(R.id.txtTitle);
            //timeTv = itemView.findViewById(R.id.txtTime);
            progressBar = itemView.findViewById(R.id.progressBar);


        }
    }
}
