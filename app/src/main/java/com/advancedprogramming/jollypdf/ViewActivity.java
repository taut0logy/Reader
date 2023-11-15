package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class ViewActivity extends AppCompatActivity implements JumpToPageFragment.JumpToPageListener {
    private boolean barsVisible = true;
    private ConstraintLayout topBar,bottomBar;
    private TextView tvBookName,tvAuthorName,tvTotalPages,tvCurrPage,etCurrPage;
    private Button showDialog;
    private ImageButton toggleDark,infobtn;
    private ConstraintLayout dialog;
    private PDFView pdfView;
    private String location;
    private boolean isNight=false;
    private int nowPage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        pdfView=findViewById(R.id.pdfView);
        topBar=findViewById(R.id.topBar);
        bottomBar=findViewById(R.id.bottomBar);
        tvBookName=findViewById(R.id.tvBookName);
        tvAuthorName=findViewById(R.id.tvAuthorName);
        tvTotalPages=findViewById(R.id.tvTotalPage);
        etCurrPage=findViewById(R.id.etCurrentPage);
        toggleDark=findViewById(R.id.toggleDark);
        infobtn=findViewById(R.id.infobtn);
        showDialog=findViewById(R.id.showDialog);
        dialog=findViewById(R.id.dialog);

        String book= getIntent().getStringExtra("Extra_book");
        String bookName=getIntent().getStringExtra("Extra_bookName");
        String authorName=getIntent().getStringExtra("Extra_authorName");
        int totalPages=getIntent().getIntExtra("Extra_totalPages",0);
        int currPage=getIntent().getIntExtra("Extra_currPage",0);
        location="/storage/emulated/0/JollyRead/BookData/"+bookName+".json";
        tvBookName.setText(bookName);
        tvAuthorName.setText(authorName);
        tvTotalPages.setText(String.valueOf(totalPages));
        File file=new File(book);
        PDFView.Configurator configurator=pdfView.fromFile(file);
        //Log.e("PDFErr","View: "+book+" "+bookName+" "+authorName+" "+totalPages+" "+currPage);
        configurator.defaultPage(currPage);
        configurator.load();
        configurator.scrollHandle(new com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle(this));
        configurator.onPageChange(new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                etCurrPage.setText(String.valueOf(page+1));
                nowPage=page;
            }
        });

        configurator.onPageScroll(new com.github.barteksc.pdfviewer.listener.OnPageScrollListener() {
            @Override
            public void onPageScrolled(int page, float positionOffset) {
                //get direction of scroll
                if(positionOffset>0) {
                    //scrolling down
                    if(barsVisible) {
                        hideBarsWithAnimation();
                    }
                }
                else {
                    //scrolling up
                    if(!barsVisible) {
                        showBarsWithAnimation();
                    }
                }
            }
        });
        configurator.onTap(new com.github.barteksc.pdfviewer.listener.OnTapListener() {
            @Override
            public boolean onTap(MotionEvent e) {
                toggleBarsVisibility();
                return true;
            }
        });

        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJumpToPageDialog(totalPages,currPage);

            }
        });

        infobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewActivity.this,InfoActivity.class);
                intent.putExtra("Extra_bookName",bookName);
                intent.putExtra("Extra_authorName",authorName);
                startActivity(intent);
            }
        });

        toggleDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNight==false){
                    configurator.nightMode(true);
                    isNight=true;
                }
                else {
                    configurator.nightMode(false);
                    isNight=false;
                }
                //pdfView.recycle();
                //configurator.load();
            }
        });

    }


    @Override
    protected void onPause() {
        try {
            BookJSON bookJSON=new BookJSON(location);
            bookJSON.setCurrePage(nowPage);
            //Log.e("PDFErr","onPause: "+bookJSON.getCurrePage());
            JSONHandler.writeJSON(location,bookJSON);
        } catch (Exception e){
            Log.e("PDFErr","onPause: "+e.getMessage());
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            BookJSON bookJSON=new BookJSON(location);
            bookJSON.setCurrePage(nowPage);
            //Log.e("PDFErr","onStop: "+bookJSON.getName());
            JSONHandler.writeJSON(location,bookJSON);
        } catch (Exception e){
            Log.e("PDFErr","onStop: "+e.getMessage());
        }
        super.onStop();
    }

    private void showJumpToPageDialog(int n,int m) {
        JumpToPageFragment dialogFragment = new JumpToPageFragment(n,m);
        dialogFragment.setJumpToPageListener(this);
        dialogFragment.show(getSupportFragmentManager(), "JumpToPageDialogFragment");
    }

    @Override
    public void onJumpToPage(int pageNumber) {
        // Handle jumping to the specified page here
        pdfView.jumpTo(pageNumber - 1);
    }
    private void toggleBarsVisibility() {
        if (barsVisible) {
            hideBarsWithAnimation();
        } else {
            showBarsWithAnimation();
        }
        barsVisible = !barsVisible;
    }

    private void showBarsWithAnimation() {
        topBar.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.VISIBLE);

        topBar.animate().translationY(0).setDuration(300).start();
        bottomBar.animate().translationY(0).setDuration(300).start();
    }

    private void hideBarsWithAnimation() {
        topBar.animate().translationY(-topBar.getHeight()-100).setDuration(300).start();
        bottomBar.animate().translationY(bottomBar.getHeight()+100).setDuration(300).start();
    }

}