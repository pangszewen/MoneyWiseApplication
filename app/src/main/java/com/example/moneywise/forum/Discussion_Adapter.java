package com.example.moneywise.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Discussion_Adapter extends RecyclerView.Adapter<Discussion_Adapter.Discussion_AdapterVH> {
    FirebaseFirestore db;
    ArrayList<ForumComment> forumComments = new ArrayList<>();
    Context context;
    public Discussion_Adapter(Context context, ArrayList<ForumComment> forumComments){
        this.forumComments = forumComments;
        this.context = context;
    }
    @NonNull
    @Override
    public Discussion_AdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.forum_discussion_row, parent, false);
        return new Discussion_AdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Discussion_AdapterVH holder, int position) {
        ForumComment forumComment = forumComments.get(position);
        LocalDateTime commentDate = forumComment.getDatePosted();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedCommentDate = commentDate.format(formatter);
        String content = forumComment.getContent();

        holder.commentDatePosted.setText(formattedCommentDate);
        holder.commentContent.setText(content);
        db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("USER_DETAILS").document(forumComment.getUserID());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //User user = convertDocumentToUser(documentSnapshot);
                    //holder.commentUsername.setText(user.getName());
                    holder.commentUsername.setText(documentSnapshot.get("name").toString());
                } else {
                    Toast.makeText(context, "user not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumComments.size();
    }

    public static class Discussion_AdapterVH extends RecyclerView.ViewHolder{
        TextView commentUsername, commentDatePosted, commentContent;
        public Discussion_AdapterVH(@NonNull View itemView) {
            super(itemView);
            commentUsername = itemView.findViewById(R.id.discussion_username);
            commentDatePosted = itemView.findViewById(R.id.discussion_datePosted);
            commentContent = itemView.findViewById(R.id.discussion_content);
        }
    }

    /*
    public User convertDocumentToUser(DocumentSnapshot dc){
        User user = new User();
        user.setUserID(dc.getId().toString());
        user.setName(dc.get("name").toString());
        user.setGender(dc.get("gender").toString());
        user.setAge((Long)dc.get("age"));
        user.setQualification(dc.get("qualification").toString());
        user.setRole(dc.get("role").toString());

        return user;
    }

     */

}
