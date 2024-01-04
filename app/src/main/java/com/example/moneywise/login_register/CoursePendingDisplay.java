package com.example.moneywise.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.example.moneywise.quiz.Course;
import com.example.moneywise.quiz.CoursesAdapter;
import com.example.moneywise.quiz.activity_course_display;
import com.example.moneywise.quiz.activity_create_course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CoursePendingDisplay extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CoursesAdapter coursesAdapter;
    FirebaseFirestore db;
    List<Course> courseList;
    SwipeRefreshLayout RVCourseRefresh;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_pending_display);
        db = FirebaseFirestore.getInstance();
        RVCourseRefresh = findViewById(R.id.RVCourseRefresh);
        recyclerView = findViewById(R.id.course_recycle_view);
        backButton = findViewById(R.id.backButton);
        setUpRVCourse();

        RVCourseRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVCourse();
                RVCourseRefresh.setRefreshing(false);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void setUpRVCourse() {
        courseList = new ArrayList<>();
        CollectionReference coursesRef = db.collection("COURSE_PENDING");

        coursesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Course> listOfCourse = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()){
                    Course course = convertDocumentToListOfCourse(dc);
                    listOfCourse.add(course);
                }
                coursesAdapter = new CoursesAdapter(CoursePendingDisplay.this, listOfCourse);
                coursesAdapter.loadBookmarkedCourses();
                prepareRecyclerView(CoursePendingDisplay.this, recyclerView, listOfCourse);
                coursesAdapter.loadBookmarkedCourses();
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Course> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Course> object){
        coursesAdapter = new CoursesAdapter(context, object);
        RV.setAdapter(coursesAdapter);
    }

    public Course convertDocumentToListOfCourse(QueryDocumentSnapshot dc){
        Course course = new Course();
        course.setCourseID(dc.getId());
        course.setCourseTitle(dc.get("title").toString());
        course.setAdvisorID(dc.get("advisorID").toString());
        if (dc.get("description") != null) {
            course.setCourseDesc(dc.get("description").toString());
            course.setCourseLanguage(dc.get("language").toString());
            course.setCourseLevel(dc.get("level").toString());
            course.setCourseMode(dc.get("mode").toString());
            course.setDateCreated(dc.get("dateCreated").toString());
        }
        return course;
    }
}
