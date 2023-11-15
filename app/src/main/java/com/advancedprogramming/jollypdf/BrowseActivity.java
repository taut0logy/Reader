package com.advancedprogramming.jollypdf;

import static android.app.ProgressDialog.show;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity {
    static int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        recyclerView = findViewById(R.id.recycler_view_browser);
        toolbar = findViewById(R.id.browser_toolbar);
        navigationView = findViewById(R.id.navDrawer);
        drawer = findViewById(R.id.drawer_layout);


        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        getPermissions();

        ArrayList<Book> pdfFiles = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString()+"/JollyRead");
        Log.e("PDFErr", "onCreate: "+directory.getAbsolutePath() );
        getPdfFiles(directory,pdfFiles);
        String name=getIntent().getStringExtra("Extra_username");
        Log.e("PDFErr", "onCreate: "+name );
//        for(Book book: pdfFiles) {
//            Log.e("PDFErr", book.getPdf() + " " + book.getName()+ " " + book.getTotalpages()+ " " + book.getCurrpage());
//        }
        if (pdfFiles.isEmpty()) {
            Toast.makeText(this, "No PDF file found", Toast.LENGTH_SHORT).show();
        } else {
            BookAdapter pdfAdapter = new BookAdapter(pdfFiles, this);
            recyclerView.setAdapter(pdfAdapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

                if(id==R.id.profile) {
                    Intent i=new Intent(BrowseActivity.this,ProfileActivity.class);
                    startActivity(i);
                } else if(id==R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    File file=new File("/storage/emulated/0/JollyRead/UserData/"+name+".json");
                    file.delete();
                    Intent i=new Intent(BrowseActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                } else if(id==R.id.share) {
                    Intent i=new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String shareBody="Your body here";
                    String shareSub="Your Subject here";
                    i.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                    i.putExtra(Intent.EXTRA_TEXT,shareBody);
                    startActivity(Intent.createChooser(i,"Share using"));
                } else if(id==R.id.about) {
                    Toast.makeText(BrowseActivity.this, "About me", Toast.LENGTH_SHORT).show();
                    //Launch LinkedIn
                    Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.linkedin.com/in/raufun-ahsan-7b1b3a1b2/"));
                    startActivity(i);
                    finish();
                } else if(id==R.id.feedback) {
                    //open gmail
                    Intent i=new Intent(Intent.ACTION_SEND);
                    i.setData(Uri.parse("mailto:"));
                    String[] to={"raufun.ahsan@gmail.com" };
                    i.putExtra(Intent.EXTRA_EMAIL,to);
                    i.putExtra(Intent.EXTRA_SUBJECT,"Feedback");
                    i.putExtra(Intent.EXTRA_TEXT,"");
                    i.setType("message/rfc822");
                    Intent chooser=Intent.createChooser(i,"Launch Email");
                    startActivity(chooser);
                }
            return true;
        });

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.search) {
                Toast.makeText(BrowseActivity.this, "Search", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.refresh) {
                getPdfFiles(directory, pdfFiles);
            } else if (id == R.id.exit) {
                Toast.makeText(BrowseActivity.this, "Exit", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        });

        //handle selecting item from recycler view

    }

//    public void onItemClick(Book book) {
//        Intent intent = new Intent(BrowseActivity.this, ViewActivity.class);
//        intent.putExtra("book", book);
//        startActivity(intent);
//    }

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
            String name=book.getName().substring(0,book.getName().length()-4);
            File file=new File("/storage/emulated/0/JollyRead/BookData/"+name+".jpeg");
            if(!file.exists()){
                try(FileOutputStream out=new FileOutputStream(file)){
                    thumbnail.compress(Bitmap.CompressFormat.JPEG,100,out);
            } catch (Exception e) {
                Log.e("PDFErr","ExtractWrite: "+ e.getMessage());
            }
            }
            // Set total pages
            book.setTotalpages(pdfRenderer.getPageCount());
            // Close the page
            page.close();
            // Close the PdfRenderer
            pdfRenderer.close();
            BookJSON bookJSON = new BookJSON();
            String url="/storage/emulated/0/JollyRead/BookData/"+book.getName()+".json";
            String err="JSONErr";
            try {
                JSONHandler.readJSON(url,bookJSON);
            } catch (Exception e) {
                Log.e("PDFErr", "ExtractRead: "+e.getMessage());
                err=e.getMessage();
            }
            //Log.e("PDFErr", "ExtractRead: "+err);
            if(err.contains("No such file or directory")){
                //File file=new File(url);
                //file.createNewFile();
                book.setCurrpage(0);
                book.setAuthor("Unknown");
                book.setGenre("Unknown");
                book.setDescription("Unknown");
                bookJSON.setName(book.getName());
                bookJSON.setAuthor(book.getAuthor());
                bookJSON.setGenre(book.getGenre());
                bookJSON.setDescription(book.getDescription());
                bookJSON.setPdfName(book.getName());
                bookJSON.setCurrePage(book.getCurrpage());
                try {
                    JSONHandler.writeJSON(url,bookJSON);
                } catch (Exception e) {
                    Log.e("PDFErr","ExtractWrite: "+ e.getMessage());
                }
            } else {
                book.setCurrpage(bookJSON.getCurrePage());
                book.setAuthor(bookJSON.getAuthor());
                book.setGenre(bookJSON.getGenre());
                book.setDescription(bookJSON.getDescription());
            }

        } catch (Exception e) {
            Log.e("PDFErr","ExtractOut: "+ e.getMessage());
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.browser_toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        }
        else
            super.onBackPressed();
    }



}