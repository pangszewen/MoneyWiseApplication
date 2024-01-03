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

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>{

    ArrayList<Article> articleList;
    NewsRecyclerAdapter(ArrayList<Article> articleList){
        this.articleList = articleList;
    }

    public void setFilteredList(ArrayList<Article> filteredList){
        this.articleList = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.titleTextView.setText(article.getTitle());
        holder.sourceTextView.setText(article.getSource().getName());
        holder.dateTextView.setText(article.getPublishedAt());
        Picasso.get().load(article.getUrlToImage())
                .error(R.drawable.no_image_icon)
                .placeholder(R.drawable.no_image_icon)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),NewsFullActivity.class);
            intent.putExtra("url", article.getUrl());
            v.getContext().startActivity(intent);
            ((Activity) v.getContext()).finish();
        });
    }

    void updateData(List<Article> data){
        articleList.clear();
        articleList.addAll(data);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, sourceTextView, dateTextView;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txtNewsTitle);
            sourceTextView = itemView.findViewById(R.id.txtAuthorName);
            imageView = itemView.findViewById(R.id.imageNews);
            dateTextView = itemView.findViewById(R.id.txtDate);
        }
    }
}
