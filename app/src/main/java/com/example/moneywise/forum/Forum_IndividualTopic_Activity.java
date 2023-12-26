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
import android.view.inputmethod.InputMethodManager;
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
    FirebaseAuth auth;
    FirebaseUser user;
    Discussion_Adapter discussionAdapter;
    TopicImage_Adapter topicImageAdapter;
    TextView TVSubject, TVAuthor, TVDatePosted, TVDescription, TVNumberOfDiscussion, TVNumberOfComment, TVNumberOfLikes;
    ImageButton backButton_individualTopic, likeButton, btn_delete;
    ClearableAutoCompleteComment comment_box;
    RecyclerView RVIndividualTopicDiscussion, RVTopicImage;
    SwipeRefreshLayout RVIndividualTopicRefresh;
    Firebase_Forum firebase = new Firebase_Forum();
    String userID, previousClass;
    ForumTopic topic = new ForumTopic();
    CommentTopic commentTopic = new CommentTopic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_individual_topic);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //userID = user.getUid();
        userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
        TVSubject = findViewById(R.id.TVSubject);
        TVAuthor = findViewById(R.id.TVAuthor);
        TVDatePosted = findViewById(R.id.TVDatePosted);
        TVDescription = findViewById(R.id.TVDescription);
        TVNumberOfDiscussion = findViewById(R.id.TVNumberOfDiscussion);
        TVNumberOfComment = findViewById(R.id.TVNumberOfComment);
        TVNumberOfLikes = findViewById(R.id.TVNumberOfLikes);
        backButton_individualTopic = findViewById(R.id.backButton_individualTopics);
        btn_delete = findViewById(R.id.btn_delete);
        RVIndividualTopicDiscussion = findViewById(R.id.RVIndividualTopicDiscussion);
        RVTopicImage = findViewById(R.id.RVTopicImage);
        RVIndividualTopicRefresh = findViewById(R.id.RVIndividualTopicRefresh);
        likeButton = findViewById(R.id.likeButton);
        comment_box = findViewById(R.id.comment_box);

        topic.setTopicID(getIntent().getStringExtra("topicID"));
        topic.setUserID(getIntent().getStringExtra("userID"));
        topic.setDatePosted(getIntent().getStringExtra("datePosted"));
        topic.setSubject(getIntent().getStringExtra("subject"));
        topic.setDescription(getIntent().getStringExtra("description"));
        topic.setLikes(getIntent().getStringExtra("likes"));
        topic.setCommentID(getIntent().getStringExtra("commentID"));
        previousClass = getIntent().getStringExtra("class");

        // Setting individual topic layout
        setTVAuthor();
        setTVSubject();
        setTVDescription();
        setTVDatePosted();
        setTVNumberOfLikes(this.topic);
        setTVNumberOfComments(this.topic);
        setRVIndividualTopicDiscussion();
        setRVTopicImage();
        setLikeButtonDrawable(this.topic);
        setVisibilityOfDeleteButton();

        comment_box.setOnClearListener(new ClearableAutoCompleteComment.OnClearListener() {
            @Override
            public void onClear() {
                toggleComment();
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
                backToPreviousActivity();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteTopic deleteTopic = new DeleteTopic();
                deleteTopic.showDeleteConfirmationDialog(Forum_IndividualTopic_Activity.this, topic, new DeleteTopic.ConfirmationDialogCallback() {
                    @Override
                    public void onConfirmation(boolean status) {
                        if(status) {
                            deleteTopic.deleteTopic(Forum_IndividualTopic_Activity.this, topic);
                            backToPreviousActivity();
                        }
                    }
                });
            }
        });

        RVIndividualTopicRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRVIndividualTopicDiscussion();
                RVIndividualTopicRefresh.setRefreshing(false);
            }
        });
        RVIndividualTopicDiscussion.setNestedScrollingEnabled(false);
    }

    public void setTVAuthor(){
        firebase.getUser(topic.getUserID(), new Firebase_Forum.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                TVAuthor.setText(user.getName());
            }
        });
    }

    public void setTVSubject(){
        TVSubject.setText(topic.getSubject());
    }

    public void setTVDescription(){
        TVDescription.setText(topic.getDescription());
    }

    public void setTVDatePosted(){
        DateTimeFormatter formatterString = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedTopicDate = topic.getDatePosted().format(formatterString);
        TVDatePosted.setText(TVDatePosted.getText() + formattedTopicDate);
    }

    public void setTVNumberOfLikes(ForumTopic topic){
        int likes = topic.getLikes().size();
        TVNumberOfLikes.setText(String.valueOf(likes));
    }

    public void setTVNumberOfComments(ForumTopic topic){
        int comments = topic.getCommentID().size();
        TVNumberOfComment.setText(String.valueOf(comments));
        TVNumberOfDiscussion.setText("(" + String.valueOf(comments) + ")");
    }

    public void setRVIndividualTopicDiscussion(){
        firebase.getForumTopic(topic.getTopicID(), new Firebase_Forum.ForumTopicCallback() {
            @Override
            public void onForumTopicReceived(ForumTopic topic) {
                setTVNumberOfComments(topic);
                filterForumComments(topic.getCommentID());
            }
        });
    }

    public void filterForumComments(List<String> commentID){
        firebase.getForumComments(new Firebase_Forum.ForumCommentsCallback() {
            @Override
            public void onForumComments(ArrayList<ForumComment> forumComments) {
                ArrayList<ForumComment> discussionItems = new ArrayList<>();
                for(ForumComment comment : forumComments){
                    if(commentID.contains(comment.getCommentID()))
                        discussionItems.add(comment);
                }
                prepareRVDiscussion(Forum_IndividualTopic_Activity.this, RVIndividualTopicDiscussion, discussionItems);
            }
        });
    }

    public void setRVTopicImage(){
        firebase.getTopicImages(topic.getTopicID(), new Firebase_Forum.TopicImagesCallback() {
            @Override
            public void onTopicImagesReceived(String[] images) {
                prepareRVImage(Forum_IndividualTopic_Activity.this, RVTopicImage, images);
            }
        });
    }

    public void setVisibilityOfDeleteButton(){
        firebase.getUser(userID, new Firebase_Forum.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                if (topic.getUserID().equals(user.getUserID()) || user.getRole().equals("Admin"))
                    btn_delete.setVisibility(View.VISIBLE);
                else
                    btn_delete.setVisibility(View.GONE);
            }
        });
    }

    // Comment topic
    protected void toggleComment() {
            String comment = comment_box.getText().toString();
            if(!TextUtils.isEmpty(comment)){
                commentTopic.createComment(topic.getTopicID(), userID, comment, Forum_IndividualTopic_Activity.this);
                comment_box.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(comment_box.getWindowToken(), 0);

            }else{
                Toast.makeText(this, "No comment", Toast.LENGTH_SHORT).show();
            }
    }

    // Like topic methods
    public void pressedLikeButton(){
        firebase.getForumTopic(topic.getTopicID(), new Firebase_Forum.ForumTopicCallback() {
            @Override
            public void onForumTopicReceived(ForumTopic topic) {
                List<String> likes = topic.getLikes();
                updateLikeInDatabase(likes);
                changeLikesOfTopic();
            }
        });
    }

    public void changeLikesOfTopic(){
        firebase.getForumTopic(topic.getTopicID(), new Firebase_Forum.ForumTopicCallback() {
            @Override
            public void onForumTopicReceived(ForumTopic topic) {
                setTVNumberOfLikes(topic);
                setLikeButtonDrawable(topic);
            }
        });
    }

    public void updateLikeInDatabase(List<String> likes){
        if(likes.contains(userID)){
            firebase.deleteLike(topic.getTopicID());
        }else{
            firebase.addLike(topic.getTopicID());
        }
    }

    public void setLikeButtonDrawable(ForumTopic topic){
        List<String> likes = topic.getLikes();
        if(!likes.contains(userID)){
            likeButton.setImageResource(R.drawable.outline_thumb_up_black);
        }else{
            likeButton.setImageResource(R.drawable.baseline_thumb_up_black);
        }
    }

    public void backToPreviousActivity(){
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

    // Prepare RV methods
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
}