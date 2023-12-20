package com.example.moneywise.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
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
import java.util.List;

public class Forum_MyTopic_Activity extends AppCompatActivity {
    MyTopic_Adapter myTopicAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    String userID;
    RecyclerView RVMyTopics;
    ImageButton backButton_myTopic, btn_createTopic;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Forum_MainActivity forumMainActivity = new Forum_MainActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_my_topic);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //userID = user.getUid();
        userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
        RVMyTopics = findViewById(R.id.RVMyTopics);
        backButton_myTopic = findViewById(R.id.backButton_myTopics);
        btn_createTopic = findViewById(R.id.btn_createTopic);
        progressBar = findViewById(R.id.progressBar);

        backButton_myTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forum_MyTopic_Activity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        btn_createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forum_MyTopic_Activity.this, Forum_CreateTopic_Activity.class);
                intent.putExtra("class", Forum_MyTopic_Activity.class.toString());
                startActivity(intent);
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
                prepareRecyclerView(Forum_MyTopic_Activity.this, RVMyTopics, myTopicList);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<ForumTopic> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<ForumTopic> object){
        myTopicAdapter = new MyTopic_Adapter(context, object);
        RV.setAdapter(myTopicAdapter);
    }
}