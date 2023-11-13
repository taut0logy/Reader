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
