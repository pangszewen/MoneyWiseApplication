package com.example.moneywise.scholarship;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>{

    ArrayList<Article> articleList;

    // Constructor to initialize the adapter with a list of articles
    NewsRecyclerAdapter(ArrayList<Article> articleList){
        this.articleList = articleList;
    }

    // Method to set a filtered list and notify the adapter of the change
    public void setFilteredList(ArrayList<Article> filteredList){
        this.articleList = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each news item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Get the current article at the specified position
        Article article = articleList.get(position);

        // Set data to the views in the news item layout
        holder.titleTextView.setText(article.getTitle());
        holder.sourceTextView.setText(article.getSource().getName());

        // Format and display the date
        String originalDate = article.getPublishedAt();
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = originalFormat.parse(originalDate);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String formattedDate = newFormat.format(date);
            holder.dateTextView.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.dateTextView.setText(originalDate); // Show original date if parsing fails
        }

        // Load and display the image using Picasso library
        Picasso.get().load(article.getUrlToImage())
                .error(R.drawable.no_image_icon)
                .placeholder(R.drawable.no_image_icon)
                .into(holder.imageView);

        // Set click listener for the news item to open the full news article
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),NewsFullActivity.class);
            intent.putExtra("url", article.getUrl());
            intent.putExtra("originActivity", "FindNewsActivity");
            v.getContext().startActivity(intent);
            ((Activity) v.getContext()).finish();
        });
    }

    // Method to update the data in the adapter
    void updateData(List<Article> data){
        articleList.clear();
        articleList.addAll(data);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    // ViewHolder class to hold references to the views in the news item layout
    class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, sourceTextView, dateTextView;
        ImageView imageView;

        // Constructor to initialize the views in the ViewHolder
        public NewsViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txtNewsTitle);
            sourceTextView = itemView.findViewById(R.id.txtAuthorName);
            imageView = itemView.findViewById(R.id.imageNews);
            dateTextView = itemView.findViewById(R.id.txtDate);
        }
    }
}
