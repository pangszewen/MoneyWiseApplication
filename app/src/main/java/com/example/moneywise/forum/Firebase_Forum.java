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

public class Firebase_Forum {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    //String userID = user.getUid();
    String userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    CollectionReference forumTopicRef = db.collection("FORUM_TOPIC");
    CollectionReference forumCommentRef = db.collection("FORUM_COMMENT");
    CollectionReference userRef = db.collection("USER_DETAILS");
    String storageName = "FORUM_IMAGES/";

    public interface ForumTopicInOrderCallback{
        void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics);
    }

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

    public void getTopicImages(String topicID, TopicImagesCallback callback){
        StorageReference storageReference = storage.getReference(storageName + topicID);
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    final String[] images = new String[items.size()];
                    final AtomicInteger count = new AtomicInteger(0);

                    for (int i = 0; i < items.size(); i++) {
                        final int index = i;
                        items.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                images[index] = imageUri;
                                int completedCount = count.incrementAndGet();

                                if (completedCount == items.size()) {
                                    // All download URLs have been fetched
                                    callback.onTopicImagesReceived(images);
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

    public void insertForumComment(ForumComment comment, InsertForumCommentCallback callback){
        Map<String, Object> map = new HashMap<>();
        map.put("commentID", comment.getCommentID());
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

    public void updateCommentInTopic(ForumComment comment, String topicID){
        DocumentReference ref = forumTopicRef.document(topicID);
        ref.update("commentID", FieldValue.arrayUnion(comment.getCommentID())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    public interface DeleteTopicCallback{
        void onDeleteTopic(boolean status);
    }

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

    public void addLike(String topicID){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topicID);
        ref.update("likes", FieldValue.arrayUnion(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void deleteLike(String topicID){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topicID);
        ref.update("likes", FieldValue.arrayRemove(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    public interface UserCallback{
        void onUserReceived(User user);
    }

    public void getUser(String userID, UserCallback callback){
        DocumentReference userDocRef = userRef.document(userID);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = convertDocumentToUser(documentSnapshot);
                callback.onUserReceived(user);
            }
        });
    }

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

    public User convertDocumentToUser(DocumentSnapshot dc){
        User user = new User();
        user.setUserID(dc.getId().toString());
        user.setName(dc.get("name").toString());
        user.setGender(dc.get("gender").toString());
        user.setDob(dc.get("dob").toString());
        if(dc.get("qualification")!=null)
            user.setQualification(dc.get("qualification").toString());
        user.setRole(dc.get("role").toString());

        return user;
    }

    public ForumComment convertDocumentToForumComment(QueryDocumentSnapshot dc){
        ForumComment comment = new ForumComment();
        comment.setCommentID(dc.getId());
        comment.setDatePosted(dc.get("datePosted").toString());
        comment.setContent(dc.get("content").toString());
        comment.setUserID(dc.get("userID").toString());
        return comment;
    }
}
