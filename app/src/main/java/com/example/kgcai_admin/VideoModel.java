package com.example.kgcai_admin;

public class VideoModel {

    private String videoName, videoUri;

    public VideoModel() {
    }

    public VideoModel(String videoName, String videoUri) {

        if(videoName.trim().equals("")){
            videoName = "Not available";
        }

        this.videoName = videoName;
        this.videoUri = videoUri;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }
}
