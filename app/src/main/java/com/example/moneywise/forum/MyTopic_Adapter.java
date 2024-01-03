package com.example.moneywise.forum;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyTopic_Adapter extends RecyclerView.Adapter<MyTopic_Adapter.MyTopic_AdapterVH>{
    List<ForumTopic> forumTopics = new ArrayList<>();
    Firebase_Forum firebaseForum = new Firebase_Forum();
    Context context;

    public MyTopic_Adapter(Context context, List<ForumTopic> forumTopics) {
        this.forumTopics = forumTopics;
        this.context = context;
    }

    @NonNull
    @Override
    public MyTopic_Adapter.MyTopic_AdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.forum_mytopics_row, parent, false);
        return new MyTopic_Adapter.MyTopic_AdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTopic_Adapter.MyTopic_AdapterVH holder, int position) {
        ForumTopic forumTopic = forumTopics.get(position);
        String topicSubject = forumTopic.getSubject();
        List<String> topicLikes = forumTopic.getLikes();
        List<String> topicComments = forumTopic.getCommentID();
        LocalDateTime topicDate = forumTopic.getDatePosted();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedTopicDate = topicDate.format(formatter);

        firebaseForum.getFirstTopicImage(forumTopic.getTopicID(), new Firebase_Forum.FirstTopicImageCallback() {
            @Override
            public void onFirstTopicImageReceived(Uri uri) {
                String firstImageUri = uri.toString();
                if (position == holder.getAdapterPosition()) {
                    Picasso.get().load(firstImageUri).into(holder.topicImage);
                }
            }
        });

        holder.topicSubject.setText(topicSubject);
        holder.topicLikes.setText(String.valueOf(topicLikes.size()) + " likes");
        holder.topicComments.setText(String.valueOf(topicComments.size()) + " comments");
        holder.topicDate.setText(formattedTopicDate);

        Intent intent = new Intent(context, Forum_IndividualTopic_Activity.class);
        // pass data from this activity to another activity
        // must be String
        intent.putExtra("topicID", forumTopic.getTopicID());
        intent.putExtra("userID", forumTopic.getUserID());
        intent.putExtra("datePosted", forumTopic.getDatePosted().toString());
        intent.putExtra("subject", forumTopic.getSubject());
        intent.putExtra("description", forumTopic.getDescription());
        intent.putExtra("likes", forumTopic.getLikes().toString());
        intent.putExtra("commentID", forumTopic.getCommentID().toString());
        intent.putExtra("class", context.getClass().toString());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteTopic deleteTopic = new DeleteTopic();
                deleteTopic.showDeleteConfirmationDialog(context, forumTopics.get(position), new DeleteTopic.ConfirmationDialogCallback() {
                    @Override
                    public void onConfirmation(boolean status) {
                        if(status)
                            deleteTopic.deleteTopic(context, forumTopics.get(position));
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumTopics.size();
    }

    public class MyTopic_AdapterVH extends RecyclerView.ViewHolder{
        TextView topicSubject, topicLikes, topicComments, topicDate;
        ImageView topicImage;
        ImageButton btn_delete;

        public MyTopic_AdapterVH(@NonNull View itemView) {
            super(itemView);
            topicImage = itemView.findViewById(R.id.IVTopic);
            topicSubject = itemView.findViewById(R.id.topic_row_subject);
            topicLikes = itemView.findViewById(R.id.topic_row_likes);
            topicComments = itemView.findViewById(R.id.topic_row_comments);
            topicDate = itemView.findViewById(R.id.topic_row_date);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
