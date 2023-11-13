package com.advancedprogramming.jollypdf;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
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

        getPermissions();
        ArrayList<Book> pdfFiles = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString());
        getPdfFiles(directory,pdfFiles);
        for(Book book: pdfFiles) {
            Log.e("PDFErr", book.getPdf() + " " + book.getName()+ " " + book.getTotalpages());
        }
        if (pdfFiles.isEmpty()) {
            Toast.makeText(this, "No PDF file found", Toast.LENGTH_SHORT).show();
        } else {
            BookAdapter pdfAdapter = new BookAdapter(pdfFiles, this);
            recyclerView.setAdapter(pdfAdapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void getPdfFiles(File directory, ArrayList<Book> pdfFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    //Log.e("PDFErr", file.getName() + " " + file.getAbsolutePath());
                    Book book = new Book();
                    book.setName(file.getName());
                    book.setPdf(file.getAbsolutePath());
                    //Log.e("PDFErr", book.getPdf() + " " + book.getName());
                    extractPdfInformation(book);
                    pdfFiles.add(book);
                }
//                } else if (file.isDirectory()) {
//                    getPdfFiles(file, pdfFiles);
////                    if(path.endsWith("/Android/Download") || path.endsWith("/Documents")) {
////                        Log.e("PDFErr", "Skipping " + path);
////                        getPdfFiles(file, pdfFiles);
////                    } else {
////                        return;
////                    }
//                } else {
//                    return;
//                }
            }
        }
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
            // Close the page
            page.close();
            // Close the PdfRenderer
            pdfRenderer.close();

        } catch (Exception e) {
            Log.e("PDFErr", e.getMessage());
            e.printStackTrace();
        }
    }


    private void getPermissions() {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }
        //Android is 11 (R) or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                requestStoragePermission();
            }
        } else {
            //Below android 11
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }

    }
    private void requestStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }
    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(Environment.isExternalStorageManager()) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );



//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        if(requestCode == 1) {
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission denied
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }





}