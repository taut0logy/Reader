package com.advancedprogramming.jollypdf;


import java.io.Serializable;

public class User implements Serializable {
    private String id,name,email,imageURL;


    public User() {
    }
    public User(String id, String name, String email, String imageURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageURL = imageURL;
    }
    public void setUserID(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) { this.email = email; }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() { return email; }
    public String getImageURL() {
        return imageURL;
    }

}
