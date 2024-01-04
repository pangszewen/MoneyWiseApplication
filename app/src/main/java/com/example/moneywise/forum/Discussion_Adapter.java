package com.example.moneywise.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.example.moneywise.login_register.Firebase_User;
import com.example.moneywise.login_register.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Discussion_Adapter extends RecyclerView.Adapter<Discussion_Adapter.Discussion_AdapterVH> {
    Firebase_Forum firebaseForum = new Firebase_Forum();
    Firebase_User firebaseUser = new Firebase_User();
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
        firebaseUser.getUser(forumComment.getUserID(), new Firebase_Forum.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                holder.commentUsername.setText(user.getName());
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
}
