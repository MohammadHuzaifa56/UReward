package com.htech.ureward.model;

public class Userss {
    String name,mail,imageUrl;

    public Userss(String name, String mail, String imageUrl) {
        this.name = name;
        this.mail = mail;
        this.imageUrl = imageUrl;
    }

    public Userss() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

