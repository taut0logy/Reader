package com.advancedprogramming.jollypdf;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;



public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> books;
    private Context context;


    //private onItemClickListener onItemClickListener;
    public BookAdapter(List<Book> books, Context context){
        this.books = books;
        this.context = context;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_view, parent, false);
        return new BookViewHolder(view);
    }


    @NonNull
    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);
        //Log.e("PDFErr","Rec: " + book.getPdf() + " " + book.getName()+ " " + book.getTotalpages());
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());
        String pages = String.valueOf(book.getTotalpages());
        holder.pages.setText(pages);
        float progress = (book.getCurrpage()/book.getTotalpages())*100;
        holder.completed.setText(progress+"%");
        //set the bitmap here
        Glide.with(context).load(book.getImage()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = books.get(position);
                String pdf = book.getPdf();
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra("Extra_book", pdf);
                intent.putExtra("Extra_bookName", book.getName());
                intent.putExtra("Extra_authorName", book.getAuthor());
                intent.putExtra("Extra_totalPages", book.getTotalpages());
                String url="/storage/emulated/0/JollyRead/BookData/"+book.getName()+".json";
                BookJSON bookJSON=new BookJSON();
                try{
                    JSONHandler.readJSON(url,bookJSON);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PDFErr","Rec: "+e.getMessage());
                }
                intent.putExtra("Extra_currPage",bookJSON.getCurrePage());
                UserJSON userJSON=new UserJSON();
                try{
                    JSONHandler.readJSON("/storage/emulated/0/JollyRead/UserData/curr.json",userJSON);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PDFErr","Rec: "+e.getMessage());
                }
                ArrayList<String> readingHistory=userJSON.getReadingHistory();
                if(readingHistory.contains(book.getName())){
                    Log.e("PDFErr","Rec: "+book.getName());
                    readingHistory.remove(book.getName());
                }
                readingHistory.add(book.getName());
                try{
                    JSONHandler.writeJSON("/storage/emulated/0/JollyRead/UserData/curr.json",userJSON);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PDFErr","Rec: "+e.getMessage());
                }

                //Log.e("PDFErr","Rec: "+bookJSON.getCurrePage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, author, pages,completed;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.book_thumbnail);
            name = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.author_name);
            pages = itemView.findViewById(R.id.page_cnt);
            completed = itemView.findViewById(R.id.progress_num);
        }
    }
}
