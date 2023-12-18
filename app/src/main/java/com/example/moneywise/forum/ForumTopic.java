package com.example.moneywise.forum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForumTopic {
    private String topicID;
    private String userID;
    private LocalDateTime datePosted;
    private String subject;
    private String description;
    private List<String> likes;
    private List<String> commentID;

    public ForumTopic(){

    }

    public ForumTopic(String topicID, String userID, String subject, String description) {
        this.topicID = topicID;
        this.userID = userID;
        this.datePosted = LocalDateTime.now();
        this.subject = subject;
        this.description = description;
        this.likes = new ArrayList<>();
        this.commentID = new ArrayList<>();
    }

    public ForumTopic(String topicID, String userID, LocalDateTime datePosted, String subject, String description, List<String> likes, List<String> commentID) {
        this.topicID = topicID;
        this.userID = userID;
        this.datePosted = datePosted;
        this.subject = subject;
        this.description = description;
        this.likes = likes;
        this.commentID = commentID;
    }


    public String getTopicID() {
        return topicID;
    }

    public void setTopicID(String topicID) {
        this.topicID = topicID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }
    public void setDatePosted(String datePosted){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .toFormatter();
        this.datePosted = LocalDateTime.parse(datePosted, formatter);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public void setLikes(String likeString){
        if(likeString.length()>2) {
            likeString = likeString.substring(1, likeString.length() - 1);
            String[] likeArray = likeString.split(", ");
            this.likes = new ArrayList<>(Arrays.asList(likeArray));
        }else{
            this.likes = new ArrayList<>();
        }
    }

    public List<String> getCommentID() {
        return commentID;
    }

    public void setCommentID(List<String> commentID) {
        this.commentID = commentID;
    }


    public void setCommentID(String commentString){
        if(commentString.length()>2) {
            commentString = commentString.substring(1, commentString.length() - 1);
            String[] commentArray = commentString.split(", ");
            this.commentID = new ArrayList<>(Arrays.asList(commentArray));
        }else{
            this.commentID = new ArrayList<>();
        }
    }
}
