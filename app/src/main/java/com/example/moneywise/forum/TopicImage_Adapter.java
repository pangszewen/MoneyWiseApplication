package com.example.moneywise.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopicImage_Adapter extends RecyclerView.Adapter<TopicImage_Adapter.TopicImage_AdapterVH>{
    String[] images = new String[0];
    Context context;
    public TopicImage_Adapter(Context context, String[] images){
        this.images = images;
        this.context = context;
    }
    @NonNull
    @Override
    public TopicImage_AdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.forum_topic_image_row, parent, false);
        return new TopicImage_AdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicImage_AdapterVH holder , int position) {
        String image = images[position];
        Picasso.get().load(image).resize(1000, 1000).centerInside().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class TopicImage_AdapterVH extends RecyclerView.ViewHolder{
        ImageView imageView;
        public TopicImage_AdapterVH(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.IVTopic);
        }
    }
}
