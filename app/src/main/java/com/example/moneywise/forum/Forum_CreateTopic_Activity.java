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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String userID, previousClass;
    FirebaseStorage storage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Forum_MainActivity forumMainActivity = new Forum_MainActivity();
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

        DocumentReference ref = db.collection("USER_DETAILS").document(userID);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TVAccount.setText(documentSnapshot.get("name").toString());
            }
        });

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
                if(TextUtils.isEmpty(TopicSubject)){
                    Toast.makeText(Forum_CreateTopic_Activity.this, "Topic subject is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(TopicDescription)){
                    Toast.makeText(Forum_CreateTopic_Activity.this, "Topic description is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                createTopic(TopicSubject, TopicDescription);
                Intent intent = new Intent(Forum_CreateTopic_Activity.this, Forum_MainActivity.class);
                startActivity(intent);
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
        Forum_MainActivity forumMainActivity = new Forum_MainActivity();
        CollectionReference collectionReference = db.collection("FORUM_TOPIC");
        collectionReference.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                List<ForumTopic> forumTopicList = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    ForumTopic topic =  forumMainActivity.convertDocumentToForumTopic(dc);
                    forumTopicList.add(topic);
                }
                String topicID = generateTopicID(forumTopicList);
                //String userID = user.getUid();
                String userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
                ForumTopic newTopic = new ForumTopic(topicID, userID, TopicSubject, TopicDescription);
                insertTopicIntoDatabase(newTopic);
                uploadImages(newTopic.getTopicID());
            }
        });
    }

    private void uploadImages(String topicID){
        for(int i =0; i<chooseImageList.size(); i++){
            Uri image = chooseImageList.get(i);
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

    private void insertTopicIntoDatabase(ForumTopic topic){
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