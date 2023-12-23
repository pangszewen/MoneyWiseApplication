package com.example.moneywise.forum;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommentTopic {
    Firebase_Forum firebase = new Firebase_Forum();
    Random rand = new Random();
    public void createComment(String topicID, String userID, String content, Context context){
        firebase.getForumComments(new Firebase_Forum.ForumCommentsCallback() {
            @Override
            public void onForumComments(ArrayList<ForumComment> forumComments) {
                String commentID = generateCommentID(topicID, forumComments);
                ForumComment forumComment = new ForumComment(commentID, topicID, content, userID);
                insertCommentIntoDatabase(forumComment, topicID, context);
            }
        });
    }

    private void insertCommentIntoDatabase(ForumComment comment, String topicID, Context context){
        firebase.insertForumComment(comment, new Firebase_Forum.InsertForumCommentCallback() {
            @Override
            public void onInsertForumComment(boolean status) {
                if(status) {
                    Toast.makeText(context, "Comment successfully posted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Refresh discussion to view comment", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context, "Comment failed to post", Toast.LENGTH_SHORT).show();
                }
            }
        });
        firebase.updateCommentInTopic(comment, topicID);
    }

    private String generateCommentID(String topicID, ArrayList<ForumComment> comments){
        String newID;
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = topicID + "_" + String.format("%07d", randomNum); //T0001000
            if(checkDuplicatedCommentID(newID, comments))
                break;
        }
        return newID;
    }

    private boolean checkDuplicatedCommentID(String newID, ArrayList<ForumComment> comments){
        for(ForumComment comment : comments){
            if(newID.equals(comment.getCommentID()))
                return false;
        }
        return true;
    }
}
