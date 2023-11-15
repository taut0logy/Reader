package com.advancedprogramming.jollypdf;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class JSONHandler {
    static void readJSON(String filename,BookJSON bookJSON) throws Exception {
        //read json file
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String json = "";
        String line = bufferedReader.readLine();
        while (line != null) {
            json += line;
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        //parse json file
        JSONTokener jsonTokener = new JSONTokener(json);
        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonTokener);
        bookJSON.setName(jsonObject.getString("name"));
        bookJSON.setAuthor(jsonObject.getString("author"));
        bookJSON.setGenre(jsonObject.getString("genre"));
        bookJSON.setDescription(jsonObject.getString("description"));
        bookJSON.setPdfName(jsonObject.getString("pdfName"));
        bookJSON.setCurrePage(jsonObject.getInt("currePage"));
    }

    static void writeJSON(String filename,BookJSON bookJSON) throws Exception {
        //create json object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", bookJSON.getName());
        jsonObject.put("author", bookJSON.getAuthor());
        jsonObject.put("genre", bookJSON.getGenre());
        jsonObject.put("description", bookJSON.getDescription());
        jsonObject.put("pdfName", bookJSON.getPdfName());
        jsonObject.put("currePage", bookJSON.getCurrePage());
        //write json object to file
        Log.e("PDFErr",jsonObject.toString());
        FileWriter fileWriter = new FileWriter(filename,false);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();
        fileWriter.close();
    }
    static void readJSON(String filename,UserJSON userJSON) throws Exception {
        //read json file
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String json = "";
        String line = bufferedReader.readLine();
        while (line != null) {
            json += line;
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        //parse json file
        JSONTokener jsonTokener = new JSONTokener(json);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        userJSON.setUserID(jsonObject.getString("username"));
        JSONArray readingHistory = jsonObject.getJSONArray("readingHistory");
        for(int i=0;i<readingHistory.length();i++){
            userJSON.getReadingHistory().add(readingHistory.getString(i));
        }
        JSONArray readingList = jsonObject.getJSONArray("readingList");
        for(int i=0;i<readingList.length();i++){
            userJSON.getReadingList().add(readingList.getString(i));
        }
    }

    static void writeJSON(String filename,UserJSON userJSON) throws Exception {
        //create json object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userJSON.getUserID());
        JSONArray readingHistory = new JSONArray();
        for(int i=0;i<userJSON.getReadingHistory().size();i++){
            readingHistory.put(userJSON.getReadingHistory().get(i));
        }
        jsonObject.put("readingHistory", readingHistory);
        JSONArray readingList = new JSONArray();
        for(int i=0;i<userJSON.getReadingList().size();i++){
            readingList.put(userJSON.getReadingList().get(i));
        }
        jsonObject.put("readingList", readingList);
        //write json object to file
        Log.e("PDFErr",jsonObject.toString());
        FileWriter fileWriter = new FileWriter(filename,false);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();
        fileWriter.close();
    }

}
