package com.example.moneywise.forum;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class CommentTopic {
    Firebase_Forum firebaseForum = new Firebase_Forum();
    Random rand = new Random();

    // Method to create comment on topic
    public void createComment(String topicID, String userID, String content, Context context){
        firebaseForum.getForumComments(new Firebase_Forum.ForumCommentsCallback() {
            @Override
            public void onForumComments(ArrayList<ForumComment> forumComments) {
                String commentID = generateCommentID(topicID, forumComments);
                ForumComment forumComment = new ForumComment(commentID, topicID, content, userID);
                insertCommentIntoDatabase(forumComment, topicID, context);
            }
        });
    }

    // Method to insert new comment in firebase
    private void insertCommentIntoDatabase(ForumComment comment, String topicID, Context context){
        firebaseForum.insertForumComment(comment, new Firebase_Forum.InsertForumCommentCallback() {
            @Override
            public void onInsertForumComment(boolean status) {
                if(status) {
                    Toast.makeText(context, "Comment successfully posted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Please refresh", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context, "Comment failed to post", Toast.LENGTH_SHORT).show();
                }
            }
        });
        firebaseForum.updateCommentInTopic(comment, topicID);
    }

    // Method to generate unique commentID based on the list of existing comments in firebase
    private String generateCommentID(String topicID, ArrayList<ForumComment> comments){
        String newID;
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = topicID + "_" + String.format("%07d", randomNum); //eg.T0001000
            if(checkDuplicatedCommentID(newID, comments))
                break;
        }
        return newID;
    }

    // Method to check if the generated commentID is repeated in the list of comments in firebase
    // if repeated return false, else return true
    private boolean checkDuplicatedCommentID(String newID, ArrayList<ForumComment> comments){
        for(ForumComment comment : comments){
            if(newID.equals(comment.getCommentID()))
                return false;
        }
        return true;
    }
}
