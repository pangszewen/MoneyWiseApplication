package com.example.moneywise.forum;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moneywise.R;
import com.example.moneywise.login_register.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// Class that stores all methods related to forum and firebase
public class Firebase_Forum {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    CollectionReference forumTopicRef = db.collection("FORUM_TOPIC");
    CollectionReference forumCommentRef = db.collection("FORUM_COMMENT");
    String storageName = "FORUM_IMAGES/";

    public interface ForumTopicInOrderCallback{
        void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics);
    }

    // Method to get all the forum topics based on date posted in descending order from firestore
    public void getForumTopicsInOrder(ForumTopicInOrderCallback callback){
        forumTopicRef.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ForumTopic> forumTopicList = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()) {
                    ForumTopic topic = convertDocumentToForumTopic(dc);
                    forumTopicList.add(topic);
                }
                callback.onForumTopicsReceived(forumTopicList);
            }
        });
    }

    public interface ForumTopicCallback{
        void onForumTopicReceived(ForumTopic topic);
    }

    // Method to get specific topic from firestore
    public void getForumTopic(String topicID, ForumTopicCallback callback){
        DocumentReference ref = forumTopicRef.document(topicID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();
                ForumTopic topic = convertDocumentToForumTopic(dc);
                callback.onForumTopicReceived(topic);
            }
        });
    }

    public interface ForumCommentsCallback{
        void onForumComments(ArrayList<ForumComment> forumComments);
    }

    // Method to get all the forum comments based on date posted in descending order from firestore
    public void getForumComments(ForumCommentsCallback callback){
        forumCommentRef.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ForumComment> forumComments = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    ForumComment comment = convertDocumentToForumComment(dc);
                    forumComments.add(comment);
                }
                callback.onForumComments(forumComments);
            }
        });
    }

    public interface TopicImagesCallback{
        void onTopicImagesReceived(String[] images);
    }

    // Method to get the images of a specific topic from firebase storage
    public void getTopicImages(String topicID, TopicImagesCallback callback){
        StorageReference storageReference = storage.getReference(storageName + topicID);
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    final String[] IMAGES = new String[items.size()];
                    final AtomicInteger COUNT = new AtomicInteger(0);

                    for (int i = 0; i < items.size(); i++) {
                        final int index = i;
                        items.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                IMAGES[index] = imageUri;       // insert the images into the correct index of array
                                int completedCount = COUNT.incrementAndGet();   // track the number of images inserted into array

                                // if all the images has inserted into the array, pass the array of images into callback method
                                if (completedCount == items.size()) {
                                    // All download URLs have been fetched
                                    callback.onTopicImagesReceived(IMAGES);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public interface FirstTopicImageCallback{
        void onFirstTopicImageReceived(Uri uri);
    }

    // Method to get the first image of a specific topic
    public void getFirstTopicImage(String topicID, FirstTopicImageCallback callback){
        StorageReference storageReference = storage.getReference(storageName + topicID);
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
                                callback.onFirstTopicImageReceived(uri);
                            }
                        });
                    }
                }
            }
        });
    }

    public interface CreateTopicCallback{
        void onCreateTopic(boolean status);
    }

    // Method to insert new created topic into firestore
    public void createTopic(ForumTopic topic, CreateTopicCallback callback){
        Map<String, Object> map = new HashMap<>();
        map.put("userID", topic.getUserID());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = topic.getDatePosted().format(formatter);
        map.put("datePosted", formattedDateTime);
        map.put("subject", topic.getSubject());
        map.put("description", topic.getDescription());
        map.put("likes", topic.getLikes());
        map.put("commentID", topic.getCommentID());
        db.collection("FORUM_TOPIC").document(topic.getTopicID()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    callback.onCreateTopic(true);
                }else {
                    callback.onCreateTopic(false);
                }
            }
        });
    }

    public interface InsertForumCommentCallback{
        void onInsertForumComment(boolean status);
    }

    // Method to insert new comment into firestore
    public void insertForumComment(ForumComment comment, InsertForumCommentCallback callback){
        Map<String, Object> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = comment.getDatePosted().format(formatter);
        map.put("datePosted", formattedDateTime);
        map.put("content", comment.getContent());
        map.put("userID", comment.getUserID());
        forumCommentRef.document(comment.getCommentID()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    callback.onInsertForumComment(true);
                }else {
                    callback.onInsertForumComment(false);
                }
            }
        });
    }

    // Method to insert the images of a topic when a new topic is created
    public void insertForumImages(ArrayList<Uri> imageList, String topicID){
        for(int i =0; i<imageList.size(); i++){
            Uri image = imageList.get(i);
            if(image!=null){
                StorageReference reference = storage.getReference().child("FORUM_IMAGES").child(topicID);
                StorageReference imageName = reference.child("Image " + i + ".jpg");
                imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
            }
        }
    }

    // Method to update the arraylist of commentID of a specific topic
    public void updateCommentInTopic(ForumComment comment, String topicID){
        DocumentReference ref = forumTopicRef.document(topicID);
        ref.update("commentID", FieldValue.arrayUnion(comment.getCommentID())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {}
        });
    }

    public interface DeleteTopicCallback{
        void onDeleteTopic(boolean status);
    }

    // Method to deletea topic from firestore
    public void deleteTopic(String topicID, DeleteTopicCallback callback){
        DocumentReference docRef = forumTopicRef.document(topicID);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onDeleteTopic(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete the document
                        callback.onDeleteTopic(false);
                    }
                });
    }

    // Method to add a userID into the arraylist of likes of a specific topic
    public void addLike(String topicID){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topicID);
        ref.update("likes", FieldValue.arrayUnion(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    // Method to delete a userID from the arraylist of likes of a specific topic
    public void deleteLike(String topicID){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topicID);
        ref.update("likes", FieldValue.arrayRemove(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    // Method to convert query document snapshots from FORUM_TOPIC in firestore to ForumTopic object
    public ForumTopic convertDocumentToForumTopic(QueryDocumentSnapshot dc) {
        ForumTopic topic = new ForumTopic();
        topic.setTopicID(dc.getId());
        topic.setUserID(dc.get("userID").toString());
        topic.setDatePosted(dc.get("datePosted").toString());
        topic.setSubject(dc.get("subject").toString());
        topic.setDescription(dc.get("description").toString());
        // Firestore retrieves List objects as List<Object> and not as ArrayList<String>
        topic.setLikes((List<String>) dc.get("likes"));
        topic.setCommentID((List<String>) dc.get("commentID"));

        return topic;
    }

    // Method to convert document snapshots from FORUM_TOPIC in firestore to ForumTopic object
    public ForumTopic convertDocumentToForumTopic(DocumentSnapshot dc){
        ForumTopic topic = new ForumTopic();
        topic.setTopicID(dc.getId());
        topic.setUserID(dc.get("userID").toString());
        topic.setDatePosted(dc.get("datePosted").toString());
        topic.setSubject(dc.get("subject").toString());
        topic.setDescription(dc.get("description").toString());

        // cast the returned Object to Long, then convert it to an int
        topic.setLikes((List<String>)dc.get("likes"));

        // Firestore retrieves List objects as List<Object> and not as ArrayList<String>
        topic.setCommentID ((List<String>) dc.get("commentID"));

        return topic;
    }

    // Method to convert query document snapshots from FORUM_COMMENT in firestore to ForumComment object
    public ForumComment convertDocumentToForumComment(QueryDocumentSnapshot dc){
        ForumComment comment = new ForumComment();
        comment.setCommentID(dc.getId());
        comment.setDatePosted(dc.get("datePosted").toString());
        comment.setContent(dc.get("content").toString());
        comment.setUserID(dc.get("userID").toString());
        return comment;
    }
}
