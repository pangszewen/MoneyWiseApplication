package com.example.moneywise.home;

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
import com.example.moneywise.scholarship.NewsFullActivity;
import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LatestNewsAdapter extends RecyclerView.Adapter<LatestNewsAdapter.NewsViewHolder>{


    ArrayList<Article> articleList;
    LatestNewsAdapter(ArrayList<Article> articleList){
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_news_row, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.news_row_title.setText(article.getTitle());
        holder.news_row_author.setText(article.getAuthor());
        Picasso.get().load(article.getUrlToImage())
                .error(R.drawable.no_image_icon)
                .placeholder(R.drawable.no_image_icon)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsFullActivity.class);
            intent.putExtra("url", article.getUrl());
            intent.putExtra("originActivity", "HomeActivity");
            v.getContext().startActivity(intent);
            ((Activity) v.getContext()).finish();
        });
    }



    void updateData(List<Article> data){
        articleList.clear();
        articleList = new ArrayList<>(data); // Directly update the existing list
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView news_row_title, news_row_author;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView){
            super(itemView);
            news_row_title = itemView.findViewById(R.id.news_row_title);
            news_row_author = itemView.findViewById(R.id.news_row_author);
            imageView = itemView.findViewById(R.id.IVTopic);
        }
    }
}
