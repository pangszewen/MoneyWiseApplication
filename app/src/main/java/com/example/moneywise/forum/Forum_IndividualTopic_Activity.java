package com.example.moneywise.forum;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Forum_IndividualTopic_Activity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseStorage storage;
    Discussion_Adapter discussionAdapter;
    TopicImage_Adapter topicImageAdapter;
    Random rand = new Random();
    TextView TVSubject, TVAuthor, TVDatePosted, TVDescription, TVNumberOfDiscussion;
    EditText ETComment;
    ImageButton backButton_individualTopic, likeButton, btn_comment;
    ProgressBar progressBar;
    RecyclerView RVIndividualTopicDiscussion, RVTopicImage;
    SwipeRefreshLayout RVIndividualTopicRefresh;
    FirebaseAuth auth;
    FirebaseUser user;
    String userID, previousClass;
    ForumTopic topic = new ForumTopic();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_individual_topic);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
        //userID = user.getUid();
        TVSubject = findViewById(R.id.TVSubject);
        TVAuthor = findViewById(R.id.TVAuthor);
        TVDatePosted = findViewById(R.id.TVDatePosted);
        TVDescription = findViewById(R.id.TVDescription);
        TVNumberOfDiscussion = findViewById(R.id.TVNumberOfDiscussion);
        ETComment = findViewById(R.id.ETComment);
        btn_comment = findViewById(R.id.btn_postComment);
        backButton_individualTopic = findViewById(R.id.backButton_individualTopics);
        RVIndividualTopicDiscussion = findViewById(R.id.RVIndividualTopicDiscussion);
        RVTopicImage = findViewById(R.id.RVTopicImage);
        RVIndividualTopicRefresh = findViewById(R.id.RVIndividualTopicRefresh);
        likeButton = findViewById(R.id.likeButton);
        progressBar = findViewById(R.id.progressBar);

        topic.setTopicID(getIntent().getStringExtra("topicID"));
        topic.setUserID(getIntent().getStringExtra("userID"));
        topic.setDatePosted(getIntent().getStringExtra("datePosted"));
        topic.setSubject(getIntent().getStringExtra("subject"));
        topic.setDescription(getIntent().getStringExtra("description"));
        topic.setLikes(getIntent().getStringExtra("likes"));
        topic.setCommentID(getIntent().getStringExtra("commentID"));
        previousClass = getIntent().getStringExtra("class");

        db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("USER_DETAILS").document(topic.getUserID());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //User user = convertDocumentToUser(documentSnapshot);
                //TVAuthor.setText(TVAuthor.getText() + user.getName());
                TVAuthor.setText(TVAuthor.getText() + documentSnapshot.get("name").toString());
            }
        });
        TVSubject.setText(topic.getSubject());
        DateTimeFormatter formatterString = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedTopicDate = topic.getDatePosted().format(formatterString);
        TVDatePosted.setText(TVDatePosted.getText() + formattedTopicDate);
        TVDescription.setText(topic.getDescription());
        setTVNumberOfDiscussion();
        setTVNumberOfLikes();
        setLikeButtonColor();

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = ETComment.getText().toString();
                if(!TextUtils.isEmpty(comment)){
                    progressBar.setVisibility(View.VISIBLE);
                    createComment(topic, comment);
                    ETComment.setText(null);
                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressedLikeButton();
                likeButton.setEnabled(true);
            }
        });

        backButton_individualTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forum_IndividualTopic_Activity.this, HomeActivity.class);
                if(previousClass.equals(Forum_MyTopic_Activity.class.toString())){
                    intent = new Intent(Forum_IndividualTopic_Activity.this, Forum_MyTopic_Activity.class);
                }else if(previousClass.equals(HomeActivity.class.toString())){
                    intent = new Intent(Forum_IndividualTopic_Activity.this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        RVIndividualTopicRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVIndividualTopicDiscussion();
                setTVNumberOfDiscussion();
                RVIndividualTopicRefresh.setRefreshing(false);
            }
        });
        RVIndividualTopicDiscussion.setNestedScrollingEnabled(false);
        setUpRVIndividualTopicDiscussion();
        setUpRVTopicImage();
    }

    public void setTVNumberOfDiscussion(){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topic.getTopicID());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();
                TextView TVNumberOfDiscussion = findViewById(R.id.TVNumberOfDiscussion);
                TextView TVNumberOfComments = findViewById(R.id.TVNumberOfComment);
                ForumTopic topic = convertDocumentToForumTopic(dc);
                TVNumberOfDiscussion.setText("(" + topic.getCommentID().size() + ")");
                TVNumberOfComments.setText(String.valueOf(topic.getCommentID().size()));
            }
        });
    }

    public void setTVNumberOfLikes(){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topic.getTopicID());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();
                TextView TVNumberOfLikes = findViewById(R.id.TVNumberOfLikes);
                ForumTopic topic = convertDocumentToForumTopic(dc);
                TVNumberOfLikes.setText(String.valueOf(topic.getLikes().size()));
            }
        });
    }

    public void pressedLikeButton(){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topic.getTopicID());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();
                ForumTopic topic = convertDocumentToForumTopic(dc);
                List<String> likes = topic.getLikes();
                deleteOrAddLikeInTopic(likes);
                changeLikeButtonColor(topic);
                setTVNumberOfLikes();
            }
        });
    }

    public void deleteOrAddLikeInTopic(List<String> likes){
        if(likes.contains(userID)){
            deleteLikeInTopic();
        }else{
            addLikeInTopic();
        }
    }

    public void changeLikeButtonColor(ForumTopic topic){
        List<String> likes = topic.getLikes();
        if(likes.contains(userID)){
            likeButton.setImageResource(R.drawable.outline_thumb_up_black);
            //likeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FFFFFF")));
        }else{
            likeButton.setImageResource(R.drawable.baseline_thumb_up_black);
            //likeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDDD5C")));
        }
    }

    public void setLikeButtonColor(){
        List<String> likes = topic.getLikes();
        if(!likes.contains(userID)){
            likeButton.setImageResource(R.drawable.outline_thumb_up_black);
            //likeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FFFFFF")));
        }else{
            likeButton.setImageResource(R.drawable.baseline_thumb_up_black);
            //likeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDDD5C")));
        }
    }

    public void setUpRVIndividualTopicDiscussion(){
        CollectionReference collectionReference = db.collection("FORUM_COMMENT");
        collectionReference.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ForumComment> forumDiscussion = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    ForumComment comment = convertDocumentToForumComment(dc);
                    for(String commentID : topic.getCommentID()){
                        if(comment.getCommentID().equals(commentID)){
                            forumDiscussion.add(comment);
                        }
                    }
                }
                prepareRVDiscussion(Forum_IndividualTopic_Activity.this, RVIndividualTopicDiscussion, forumDiscussion);
            }
        });
    }

    public void setUpRVTopicImage(){
        StorageReference storageReference = storage.getReference("FORUM_IMAGES/" + topic.getTopicID());
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    Log.d("TAG", items.toString());
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
                                    Log.d("TAG", images.toString());
                                    prepareRVImage(Forum_IndividualTopic_Activity.this, RVTopicImage, images);
                                }
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
    }

    public void prepareRVDiscussion(Context context, RecyclerView RV, ArrayList<ForumComment> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapterRVDiscussion(context, RV, object);
    }

    public void preAdapterRVDiscussion(Context context, RecyclerView RV, ArrayList<ForumComment> object){
        discussionAdapter = new Discussion_Adapter(context, object);
        RV.setAdapter(discussionAdapter);
    }

    public void prepareRVImage(Context context, RecyclerView RV, String[] object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapterRVImage(context, RV, object);
    }

    public void preAdapterRVImage(Context context, RecyclerView RV, String[] object){
        topicImageAdapter = new TopicImage_Adapter(context, object);
        RV.setAdapter(topicImageAdapter);
    }

    private void createComment(ForumTopic topic, String content){
        CollectionReference collectionReference = db.collection("FORUM_COMMENT");
        collectionReference.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ForumComment> forumCommentList = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    ForumComment comment =  convertDocumentToForumComment(dc);
                    forumCommentList.add(comment);
                }
                String commentID = generateCommentID(topic, forumCommentList);
                ForumComment forumComment = new ForumComment(commentID, topic.getTopicID(), content, userID);
                insertCommentIntoDatabase(forumComment);
            }
        });
    }

    private void insertCommentIntoDatabase(ForumComment comment){
        db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("commentID", comment.getCommentID());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = comment.getDatePosted().format(formatter);
        map.put("datePosted", formattedDateTime);
        map.put("content", comment.getContent());
        map.put("userID", comment.getUserID());
        db.collection("FORUM_COMMENT").document(comment.getCommentID()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Toast.makeText(Forum_IndividualTopic_Activity.this, "Comment successfully posted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Forum_IndividualTopic_Activity.this, "Refresh discussion to view comment", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Forum_IndividualTopic_Activity.this, "Comment failed to post", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateCommentInTopic(comment);
    }

    public void updateCommentInTopic(ForumComment comment){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topic.getTopicID());
        ref.update("commentID", FieldValue.arrayUnion(comment.getCommentID())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void addLikeInTopic(){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topic.getTopicID());
        ref.update("likes", FieldValue.arrayUnion(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void deleteLikeInTopic(){
        DocumentReference ref = db.collection("FORUM_TOPIC").document(topic.getTopicID());
        ref.update("likes", FieldValue.arrayRemove(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    private String generateCommentID(ForumTopic topic, ArrayList<ForumComment> comments){
        String newID;
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = topic.getTopicID() + "_" + String.format("%07d", randomNum); //T0001000
            if(checkDuplicatedTopicID(newID, comments))
                break;
        }
        return newID;
    }

    private boolean checkDuplicatedTopicID(String newID, ArrayList<ForumComment> comments){
        for(ForumComment comment : comments){
            if(newID.equals(comment.getCommentID()))
                return false;
        }
        return true;
    }

    public ForumComment convertDocumentToForumComment(QueryDocumentSnapshot dc){
        ForumComment comment = new ForumComment();
        comment.setCommentID(dc.getId());
        comment.setDatePosted(dc.get("datePosted").toString());
        comment.setContent(dc.get("content").toString());
        comment.setUserID(dc.get("userID").toString());
        return comment;
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