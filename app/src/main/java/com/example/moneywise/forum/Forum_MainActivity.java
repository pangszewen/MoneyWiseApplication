package com.example.moneywise.forum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.example.moneywise.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Forum_MainActivity extends AppCompatActivity {
    RecyclerView RVForum;
    Forum_Adapter forumAdapter;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;
    List<ForumTopic> forumTopics;
    Button btn_myTopic;
    SwipeRefreshLayout RVForumRefresh;
    ProgressBar progressBar;
    Toolbar toolbar;

    public Forum_MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_main);
        db = FirebaseFirestore.getInstance();
        btn_myTopic = findViewById(R.id.btn_myTopic);
        RVForum = findViewById(R.id.RVForum);
        RVForumRefresh = findViewById(R.id.RVForumRefresh);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottomForumNavigationView);
        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottomNavigationView.setBackground(null);   //To eliminate shadow of navigation bar view
        MenuItem menuItemDisable = bottomNavigationView.getMenu().findItem(R.id.iconForum);
        menuItemDisable.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        setUpRVForum();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if(itemID==R.id.iconHome) {
                    startActivity(new Intent(Forum_MainActivity.this, HomeActivity.class));
                    return true;
                }else if(itemID==R.id.iconForum) {
                    startActivity(new Intent(Forum_MainActivity.this, Forum_MainActivity.class));
                    return true;
                }else if(itemID==R.id.iconExpenses) {
                    return true;
                }else if(itemID==R.id.iconCnq){
                    return true;
                }else
                    return false;
            }
        });

        btn_myTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forum_MainActivity.this, Forum_MyTopic_Activity.class));
                finish();
            }
        });

        RVForumRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVForum();
                RVForumRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.overflow_menu, menu);
        @SuppressLint("ResourceType") Menu menu1 = findViewById(R.menu.overflow_menu);
        MenuItem menuItem = menu.findItem(R.id.overflowForum);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID==R.id.overflowHome) {
            startActivity(new Intent(Forum_MainActivity.this, HomeActivity.class));
            return true;
        }else if(itemID==R.id.overflowExpenses) {
            return true;
        }else if(itemID==R.id.overflowCnq) {
            return true;
        }else if(itemID==R.id.overflowSnn){
            return true;
        }else
            return false;
    }

    public void setUpRVForum(){
        forumTopics = new ArrayList<>();
        CollectionReference collectionReference = db.collection("FORUM_TOPIC");
        collectionReference.orderBy("datePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ForumTopic> forumTopicList = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    ForumTopic topic = convertDocumentToForumTopic(dc);
                    forumTopicList.add(topic);
                }
                forumAdapter = new Forum_Adapter(Forum_MainActivity.this, forumTopicList);
                prepareRecyclerView(Forum_MainActivity.this, RVForum, forumTopicList);
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
        forumAdapter = new Forum_Adapter(context, object);
        RV.setAdapter(forumAdapter);
    }

    public ForumTopic convertDocumentToForumTopic(QueryDocumentSnapshot dc){
        ForumTopic topic = new ForumTopic();
        topic.setTopicID(dc.getId());
        topic.setUserID(dc.get("userID").toString());
        topic.setDatePosted(dc.get("datePosted").toString());
        topic.setSubject(dc.get("subject").toString());
        topic.setDescription(dc.get("description").toString());
        // Firestore retrieves List objects as List<Object> and not as ArrayList<String>
        topic.setLikes((List<String>)dc.get("likes"));
        topic.setCommentID ((List<String>) dc.get("commentID"));

        return topic;
    }

    /*
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.CLForumMain, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

     */
}