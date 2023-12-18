package com.example.moneywise.forum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForumComment {
    private String commentID;
    private LocalDateTime datePosted;
    private String content;
    private String userID;

    public ForumComment(){}

    public ForumComment(String commentID, String topicID, String content, String userID){
        this.commentID = commentID;
        this.datePosted = LocalDateTime.now();
        this.content = content;
        this.userID = userID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public void setDatePosted(String datePosted){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        this.datePosted = LocalDateTime.parse(datePosted, formatter);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
