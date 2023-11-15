package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

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
        String location="/storage/emulated/0/JollyRead/BookData/"+get+".json";

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
        fab.setOnClickListener(v -> {
            BookJSON book=new BookJSON();
            BookJSON.fetchBOOK(url,this,book);
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
}