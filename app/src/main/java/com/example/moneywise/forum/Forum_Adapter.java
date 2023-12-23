package com.example.moneywise.forum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.example.moneywise.login_register.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Forum_Adapter extends RecyclerView.Adapter<Forum_Adapter.Forum_AdapterVH> {
    ArrayList<ForumTopic> forumTopics = new ArrayList<>();
    Firebase_Forum firebase = new Firebase_Forum();
    Context context;

    public Forum_Adapter(Context context, ArrayList<ForumTopic> forumTopics) {
        this.forumTopics = forumTopics;
        this.context = context;
    }

    @NonNull
    @Override
    public Forum_AdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.forum_topic_row, parent, false);
        //progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.VISIBLE);
        return new Forum_AdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Forum_AdapterVH holder, @SuppressLint("RecyclerView") int position) {
        ForumTopic forumTopic = forumTopics.get(position);
        String topicSubject = forumTopic.getSubject();
        List<String> topicLikes = forumTopic.getLikes();
        LocalDateTime topicDate = forumTopic.getDatePosted();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedTopicDate = topicDate.format(formatter);

        firebase.getUser(forumTopic.getUserID(), new Firebase_Forum.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                holder.topicAuthor.setText("by " + user.getName());
            }
        });

        holder.topicImage.setImageResource(R.drawable.outline_image_grey);
        firebase.getFirstTopicImage(forumTopic.getTopicID(), new Firebase_Forum.FirstTopicImageCallback() {
            @Override
            public void onFirstTopicImageReceived(Uri uri) {
                String firstImageUri = uri.toString();
                if (position == holder.getAdapterPosition()) {
                    Picasso.get().load(firstImageUri).into(holder.topicImage);
                }
            }
        });

        holder.topicSubject.setText(topicSubject);
        holder.topicLikes.setText(String.valueOf(topicLikes.size()));
        holder.topicDate.setText(formattedTopicDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumTopics.size();
    }

    public class Forum_AdapterVH extends RecyclerView.ViewHolder{
        TextView topicSubject, topicLikes, topicDate, topicAuthor;
        ImageView topicImage;

        public Forum_AdapterVH(@NonNull View itemView) {
            super(itemView);
            topicImage = itemView.findViewById(R.id.IVTopic);
            topicSubject = itemView.findViewById(R.id.topic_row_subject);
            topicAuthor = itemView.findViewById(R.id.topic_row_author);
            topicLikes = itemView.findViewById(R.id.topic_row_likes);
            topicDate = itemView.findViewById(R.id.topic_row_date);
        }
    }
}
