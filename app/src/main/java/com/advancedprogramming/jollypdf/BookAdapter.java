package com.advancedprogramming.jollypdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> books;
    private Context context;
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
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());
        holder.pages.setText(book.getTotalpages());
        float progress = (book.getCurrpage()/book.getTotalpages())*100;
        holder.completed.setText(progress+"%");
        Glide.with(context).load(book.getImage()).into(holder.image);

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
