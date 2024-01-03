package com.example.moneywise.forum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.example.moneywise.login_register.Firebase_User;
import com.example.moneywise.login_register.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Forum_CreateTopic_Activity extends AppCompatActivity {
    FirebaseFirestore db;
    Random rand = new Random();
    EditText ETTopicSubject, ETTopicDescription;
    TextView TVAccount;
    Button btn_createTopic;
    ImageButton backButton_createTopic;
    ProgressBar progressBar;
    RelativeLayout pickImageButton;
    ViewPager viewPager;
    SwipeRefreshLayout createTopicRefresh;
    Uri imageUri;
    ArrayList<Uri> chooseImageList;
    ArrayList<String> urlsList;
    FirebaseAuth auth;
    FirebaseUser user;
    Firebase_Forum firebaseForum = new Firebase_Forum();
    Firebase_User firebaseUser = new Firebase_User();
    String userID, previousClass;
    FirebaseStorage storage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_create_topic);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //userID = user.getUid();
        userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
        chooseImageList = new ArrayList<>();
        urlsList = new ArrayList<>();
        previousClass = getIntent().getStringExtra("class");

        ETTopicSubject = findViewById(R.id.ETTopicSubject);
        ETTopicDescription = findViewById(R.id.ETTopicDescription);
        TVAccount = findViewById(R.id.TVAccount);
        btn_createTopic = findViewById(R.id.btn_createTopic);
        backButton_createTopic = findViewById(R.id.backButton_createTopic);
        progressBar = findViewById(R.id.progressBar);
        pickImageButton = findViewById(R.id.RLChooseImage);
        viewPager = findViewById(R.id.viewPager);
        createTopicRefresh = findViewById(R.id.createTopicRefresh);

        setTVAccount();

        createTopicRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createTopicRefresh.setRefreshing(false);
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                pickImageFromGallery();
            }
        });

        backButton_createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forum_CreateTopic_Activity.this, HomeActivity.class);
                if(previousClass.equals(Forum_MyTopic_Activity.class.toString())){
                    intent = new Intent(Forum_CreateTopic_Activity.this, Forum_MyTopic_Activity.class);
                }else if(previousClass.equals(HomeActivity.class.toString())){
                    intent = new Intent(Forum_CreateTopic_Activity.this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        btn_createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TopicSubject = ETTopicSubject.getText().toString();
                String TopicDescription = ETTopicDescription.getText().toString();
                if(!checkTopicCredentials(TopicSubject, TopicDescription)){
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                createTopic(TopicSubject, TopicDescription);
                Intent intent = new Intent(Forum_CreateTopic_Activity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    public boolean checkTopicCredentials(String subject, String description){
        if(TextUtils.isEmpty(subject)){
            Toast.makeText(Forum_CreateTopic_Activity.this, "Topic subject is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(Forum_CreateTopic_Activity.this, "Topic description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setTVAccount(){
        firebaseUser.getUser(userID, new Firebase_Forum.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                TVAccount.setText(user.getName());
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getClipData() != null){
            int count = data.getClipData().getItemCount();
            for(int i=0; i<count; i++){
                imageUri = data.getClipData().getItemAt(i).getUri();
                chooseImageList.add(imageUri);
                setViewPagerAdapter();
            }
        }
    }

    private void setViewPagerAdapter() {
        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, chooseImageList);
        viewPager.setAdapter(adapter);
    }


    private void createTopic(String TopicSubject, String TopicDescription){
        firebaseForum.getForumTopicsInOrder(new Firebase_Forum.ForumTopicInOrderCallback() {
            @Override
            public void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics) {
                String topicID = generateTopicID(forumTopics);
                ForumTopic newTopic = new ForumTopic(topicID, userID, TopicSubject, TopicDescription);
                insertTopicIntoDatabase(newTopic);
                uploadImages(newTopic.getTopicID());
            }
        });
    }

    private void uploadImages(String topicID){
        firebaseForum.insertForumImages(chooseImageList, topicID);
    }

    private void insertTopicIntoDatabase(ForumTopic topic){
        firebaseForum.createTopic(topic, new Firebase_Forum.CreateTopicCallback() {
            @Override
            public void onCreateTopic(boolean status) {
                if(status) {
                    Toast.makeText(Forum_CreateTopic_Activity.this, "Topic successfully posted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Forum_CreateTopic_Activity.this, "Refresh forum to view topic", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Forum_CreateTopic_Activity.this, "Topic failed to post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateTopicID(List<ForumTopic> topics){
        String newID = null;
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = "T" + String.format("%07d", randomNum); //T0001000
            if(checkDuplicatedTopicID(newID, topics))
                break;
        }
        return newID;
    }

    private boolean checkDuplicatedTopicID(String newID, List<ForumTopic> topics){
        for(ForumTopic topic: topics){
            if(newID.equals(topic.getTopicID()))
                return false;
        }
        return true;
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int check = ContextCompat.checkSelfPermission(Forum_CreateTopic_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(check!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Forum_CreateTopic_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }else{
                pickImageFromGallery();
            }
        }else{
            pickImageFromGallery();
        }
    }
}