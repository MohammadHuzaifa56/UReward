package com.htech.uadmin;

public class Users {
    String name,mail,imageUrl;

    public Users(String name, String mail, String imageUrl) {
        this.name = name;
        this.mail = mail;
        this.imageUrl = imageUrl;
    }

    public Users() {
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
