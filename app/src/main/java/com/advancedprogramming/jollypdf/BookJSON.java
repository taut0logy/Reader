package com.advancedprogramming.jollypdf;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BookJSON {
    private String name, author, genre, description, pdfName;
    int currePage;

    public BookJSON() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public void setCurrePage(int currePage) {
        this.currePage = currePage;
    }

    public int getCurrePage() {
        return currePage;
    }

    public BookJSON(String filename) {
        String message = "";
        try {
            JSONHandler.readJSON(filename, this);
        } catch (Exception e) {
            message = e.getMessage();
        }
    }
    public BookJSON(String name, String author, String genre, String description, String pdfName, int currPage){
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.pdfName = pdfName;
        this.currePage = currPage;
    }

    static void fetchBOOK(String url, Context context,BookJSON bookJSON) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest customJSONObjectRequest = new StringRequest(url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                bookJSON.setName(jsonObject.getString("name"));
                bookJSON.setAuthor(jsonObject.getString("author"));
                bookJSON.setGenre(jsonObject.getString("genre"));
                bookJSON.setDescription(jsonObject.getString("description"));
            } catch (JSONException e) {
                Log.e("JSONError","fetchBOOK: "+ e.getMessage());
                e.printStackTrace();
            }
        }, error -> {
            Log.e("JSONError", "fetchBOOK: "+error.getMessage());
        });
        requestQueue.add(customJSONObjectRequest);
    }
}
