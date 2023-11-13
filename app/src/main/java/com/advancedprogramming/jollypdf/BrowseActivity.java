package com.advancedprogramming.jollypdf;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity {
    static int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ArrayList<Book> pdfFiles = getPdfFiles();
        if (pdfFiles.isEmpty()) {
            Toast.makeText(this, "No PDF file found", Toast.LENGTH_SHORT).show();
        } else {
            BookAdapter pdfAdapter = new BookAdapter(pdfFiles, this);
            recyclerView.setAdapter(pdfAdapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));



    }

    private ArrayList<Book> getPdfFiles() {
        ArrayList<Book> pdfFiles = new ArrayList<>();

        File directory = new File(Environment.getExternalStorageDirectory().toString());
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    Book book = new Book();
                    book.setName(file.getName());
                    book.setPdf(file.getAbsolutePath());
                    Log.e("PDFErr", file.getAbsolutePath());

                    // Extract other information
                    extractPdfInformation(book);

                    pdfFiles.add(book);
                }
            }
        }

        return pdfFiles;
    }

    private void extractPdfInformation(Book book) {
        try {
            // Get PdfRenderer for the PDF file
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(new File(book.getPdf()), ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

            // Get the first page of the PDF for thumbnail
            PdfRenderer.Page page = pdfRenderer.openPage(0);
            Bitmap thumbnail = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(thumbnail, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            book.setImage(thumbnail);

            // Set total pages
            book.setTotalpages(pdfRenderer.getPageCount());

            // Close the PdfRenderer
            pdfRenderer.close();

        } catch (Exception e) {
            Log.e("PDFErr", e.getMessage());
            e.printStackTrace();
        }
    }


    private void getPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }





}