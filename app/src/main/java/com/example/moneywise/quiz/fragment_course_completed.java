package com.example.moneywise.quiz;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragment_course_completed extends Fragment {
    FirebaseFirestore db;
    String userID;
    private RecyclerView recyclerView;
    private CoursesCompletedContinueAdapter coursesCompletedContinueAdapter;
    List<Course> courseList;
    SwipeRefreshLayout RVForumRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_completed, container, false);

        // Initialize Firebase and UI components
        db = FirebaseFirestore.getInstance();
        RVForumRefresh = view.findViewById(R.id.RVCourseRefresh);
        recyclerView = view.findViewById(R.id.course_recycle_view);

        // Get user ID from Firebase authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();

        // Set up the RecyclerView and fetch completed courses
        setUpRVCourse();

        // Set up the SwipeRefreshLayout for refreshing the data
        RVForumRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVCourse();
                RVForumRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    // Fetch and display the completed courses
    public void setUpRVCourse() {
        courseList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("USER_DETAILS").document(userID).collection("COURSES_COMPLETED");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Course> listOfCourse = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()) {
                    Course topic = convertDocumentToListOfCourse(dc);
                    listOfCourse.add(topic);
                }
                coursesCompletedContinueAdapter = new CoursesCompletedContinueAdapter(getContext(), listOfCourse);
                prepareRecyclerView(getContext(), recyclerView, listOfCourse);
            }
        });
    }

    // Apply search filter
    public void onSearch(String s) {
        coursesCompletedContinueAdapter.getFilter().filter(s);
    }

    // Set up RecyclerView with a LinearLayoutManager
    public void prepareRecyclerView(Context context, RecyclerView RV, List<Course> object) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    // Set up the adapter for RecyclerView
    public void preAdapter(Context context, RecyclerView RV, List<Course> object) {
        coursesCompletedContinueAdapter = new CoursesCompletedContinueAdapter(context, object);
        RV.setAdapter(coursesCompletedContinueAdapter);
    }

    // Convert Firestore document to Course object
    public Course convertDocumentToListOfCourse(QueryDocumentSnapshot dc) {
        Course course = new Course();
        course.setCourseID(dc.getId());
        course.setCourseTitle(dc.get("title").toString());
        course.setAdvisorID(dc.get("advisorID").toString());
        return course;
    }
}
