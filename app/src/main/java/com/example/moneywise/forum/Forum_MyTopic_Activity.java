package com.example.moneywise.forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Forum_MyTopic_Activity extends AppCompatActivity {
    Forum_Adapter forumAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    String userID;
    RecyclerView RVMyTopics;
    ImageButton backButton_myTopic;
    FloatingActionButton fab_add_topic;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Forum_MainActivity forumMainActivity = new Forum_MainActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_my_topic);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();
        RVMyTopics = findViewById(R.id.RVMyTopics);
        backButton_myTopic = findViewById(R.id.backButton_myTopic);
        fab_add_topic = findViewById(R.id.fab_add_topic);
        progressBar = findViewById(R.id.progressBar);

        backButton_myTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forum_MyTopic_Activity.this, Forum_MainActivity.class));
            }
        });

        fab_add_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forum_MyTopic_Activity.this, Forum_CreateTopic_Activity.class));
                finish();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        CollectionReference collectionReference = db.collection("FORUM_TOPIC");
        collectionReference.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<ForumTopic> myTopicList = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    if(dc.get("userID").toString().equals(userID)) {
                        ForumTopic topic = forumMainActivity.convertDocumentToForumTopic(dc);
                        myTopicList.add(topic);
                    }
                }
                forumAdapter = new Forum_Adapter(Forum_MyTopic_Activity.this, myTopicList);
                forumMainActivity.prepareRecyclerView(Forum_MyTopic_Activity.this, RVMyTopics, myTopicList);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}