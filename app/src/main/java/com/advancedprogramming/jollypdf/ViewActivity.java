package com.advancedprogramming.jollypdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
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

public class ViewActivity extends AppCompatActivity {
    private boolean barsVisible = true;
    private ConstraintLayout topBar,bottomBar;
    private TextView tvBookName,tvAuthorName,tvTotalPages,tvCurrPage,etCurrPage;
    private Button showDialog
    private ImageButton toggleDark,infobtn;
    private ConstraintLayout dialog;
    private boolean isNight=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        PDFView pdfView=findViewById(R.id.pdfView);
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
        tvBookName.setText(bookName);
        tvAuthorName.setText(authorName);
        tvTotalPages.setText(String.valueOf(totalPages));
        File file=new File(book);
        PDFView.Configurator configurator=pdfView.fromFile(file);
        configurator.defaultPage(currPage);
        configurator.load();
        //configurator.nightMode(true).load();
        configurator.scrollHandle(new com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle(this));
        configurator.onPageChange(new com.github.barteksc.pdfviewer.listener.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                etCurrPage.setText(String.valueOf(page+1));
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
            }
        });



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