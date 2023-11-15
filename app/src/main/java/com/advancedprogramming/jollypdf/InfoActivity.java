package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.ReferenceQueue;

public class InfoActivity extends AppCompatActivity {
    private ImageView bookImage;
    private TextView bookName,authorName,genre,description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        bookImage=findViewById(R.id.imageViewbook);
        bookName=findViewById(R.id.textViewbook);
        authorName=findViewById(R.id.textViewauthor);
        genre=findViewById(R.id.textViewgenre);
        description=findViewById(R.id.textViewdescription);
        FloatingActionButton fab=findViewById(R.id.fab);
        String get=getIntent().getStringExtra("Extra_bookName");
        //String location="/storage/emulated/0/JollyRead/BookData/"+get+".json";
        String location="/Internal storage/JollyRead/BookData/"+get+".json";

        BookJSON bookJSON=new BookJSON();
        try {
            JSONHandler.readJSON(location,bookJSON);
        } catch (Exception e) {
            Log.e("PDFErr","onCreate "+e);
            e.printStackTrace();
        }
        Log.e("PDFErr","Info: "+bookJSON.getName()+" "+bookJSON.getAuthor()+" "+bookJSON.getGenre()+" "+bookJSON.getDescription());
        String name=bookJSON.getName();
        //remove the .pdf extension
        name=name.substring(0,name.length()-4);
        Log.e("PDFErr","Info: "+name);
        bookName.setText(name);
        authorName.setText(bookJSON.getAuthor());
        genre.setText(bookJSON.getGenre());
        description.setText(bookJSON.getDescription());
        //File file=new File("/storage/emulated/0/JollyRead/BookData/"+name+".jpeg");
        Bitmap bitmap= BitmapFactory.decodeFile("/storage/emulated/0/JollyRead/BookData/"+name+".jpeg");
        bookImage.setImageBitmap(bitmap);
        String url="https://www.googleapis.com/books/v1/volumes?q="+bookJSON.getName()+"&maxResults=1";
        String finalName = name;
        fab.setOnClickListener(v -> {
            BookJSON book=new BookJSON();
            fetch(url, finalName,book,this);

            try {
                JSONHandler.writeJSON(location,book);
            } catch (Exception e) {
                Log.e("PDFErr","onCreate "+e);
            }
            bookName.setText(book.getName());
            authorName.setText(book.getAuthor());
            genre.setText(book.getGenre());
            description.setText(book.getDescription());
            String str=book.getName().substring(0,book.getName().length()-4);
            try {
                JSONHandler.writeJSON("/storage/emulated/0/JollyRead/BookData/"+str+".json",book);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void fetch(String url, String name, BookJSON bookJSON, Context context){
        RequestQueue referenceQueue= Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(url, response -> {
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    String bookName=jsonObject1.getString("name");
                    if(bookName.equals(name)){
                        String author=jsonObject1.getString("author");
                        String genre=jsonObject1.getString("genre");
                        String description=jsonObject1.getString("description");
                        bookJSON.setName(bookName);
                        bookJSON.setAuthor(author);
                        bookJSON.setGenre(genre);
                        bookJSON.setDescription(description);
                        break;
                    }
                }

            } catch (Exception e) {
                Log.e("PDFErr","onCreate "+e);
            }
        }, error -> {

        });

        //fetch the data from the url
    }
}