package com.example.kgcai_admin.helper;

public class ModelVideo {

    String ID, Title, TimeStamp, VideoUrl;

    public ModelVideo() {
        //firebase requires empty constructor
    }

    public ModelVideo(String ID, String title, String timeStamp, String videoUrl) {
        this.ID = ID;
        Title = title;
        TimeStamp = timeStamp;
        VideoUrl = videoUrl;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }
}
