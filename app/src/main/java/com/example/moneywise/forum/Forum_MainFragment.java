package com.example.moneywise.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Forum_MainFragment extends Fragment {

    RecyclerView RVForum;
    Forum_Adapter forumAdapter;
    FirebaseFirestore db;
    List<ForumTopic> forumTopics;
    Button btn_myTopic;
    SwipeRefreshLayout RVForumRefresh;
    ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_forum__main, container, false);
            db = FirebaseFirestore.getInstance();
            btn_myTopic = rootview.findViewById(R.id.MyTopics);
            RVForum = rootview.findViewById(R.id.RVForum);
            RVForumRefresh = rootview.findViewById(R.id.RVForumRefresh);
            progressBar = rootview.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            setUpRVForum();

            btn_myTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), Forum_MyTopic_Activity.class));
                }
            });

            RVForumRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    setUpRVForum();
                    RVForumRefresh.setRefreshing(false);
                }
            });
        return rootview;
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
                forumAdapter = new Forum_Adapter(getActivity(), forumTopicList);
                prepareRecyclerView(getActivity(), RVForum, forumTopicList);
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
}