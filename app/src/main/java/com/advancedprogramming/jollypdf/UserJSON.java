package com.advancedprogramming.jollypdf;

import java.util.ArrayList;

public class UserJSON {
    private String userID;
    ArrayList<String> readingHistory;
    ArrayList<String> readingList;

    public UserJSON(){
        readingHistory = new ArrayList<>();
        readingList = new ArrayList<>();
    }

    public UserJSON(String username, ArrayList<String> readingHistory, ArrayList<String> readingList) {
        this.userID = username;
        this.readingHistory = readingHistory;
        this.readingList = readingList;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getReadingHistory() {
        return readingHistory;
    }

    public void setReadingHistory(ArrayList<String> readingHistory) {
        this.readingHistory = readingHistory;
    }

    public ArrayList<String> getReadingList() {
        return readingList;
    }

    public void setReadingList(ArrayList<String> readingList) {
        this.readingList = readingList;
    }
}
