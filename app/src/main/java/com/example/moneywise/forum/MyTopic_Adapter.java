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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class MyTopic_Adapter extends RecyclerView.Adapter<MyTopic_Adapter.MyTopic_AdapterVH>{
    List<ForumTopic> forumTopics = new ArrayList<>();
    FirebaseStorage storage;
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

        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("FORUM_IMAGES/" + forumTopic.getTopicID());
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();

                    if (!items.isEmpty()) {
                        // Get the first item (image) in the folder
                        items.get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String firstImageUri = uri.toString();
                                Picasso.get().load(firstImageUri).into(holder.topicImage);
                                // Process the first image URI as needed
                                // Here you can use the URI or load the image
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure if needed
                            }
                        });
                    }
                }
            }
        });

        holder.topicSubject.setText(topicSubject);
        holder.topicLikes.setText(String.valueOf(topicLikes.size()) + " likes");
        holder.topicComments.setText(String.valueOf(topicComments.size()) + " comments");
        holder.topicDate.setText(formattedTopicDate);

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //developing
            }
        });

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
