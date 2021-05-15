package com.htech.ureward.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class VideoModel {
    String url;
    @ServerTimestamp
    Date date;
    int type;

    public VideoModel(String url, Date date, int type) {
        this.url = url;
        this.date = date;
        this.type=type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public VideoModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
