package com.example.moneywise.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
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

public class activity_course_bookmark extends AppCompatActivity {
    ImageButton back;
    ArrayList<Course> courseList;
    ArrayList<Quiz> quizList;
    FirebaseFirestore db;
    String userID;
    RecyclerView recyclerViewCourse;
    RecyclerView recyclerViewQuiz;
    private CoursesAdapter coursesAdapter;
    private QuizzesAdapter quizzesAdapter;
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_bookmark);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.backButton);
        recyclerViewCourse = findViewById(R.id.RVCourse);
        recyclerViewQuiz = findViewById(R.id.RVQuiz);
        refreshLayout = findViewById(R.id.SRLBookmark);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVCourse();
                setUpRVQuiz();
                refreshLayout.setRefreshing(false);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPreviousActivity();
            }
        });
        setUpRVCourse();
        setUpRVQuiz();
    }

    public void setUpRVCourse() {
        courseList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("USER_DETAILS").document(userID).collection("COURSES_BOOKMARK");

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Course> listOfCourse = new ArrayList<>();
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                Course topic = convertDocumentToListOfCourse(dc);
                                listOfCourse.add(topic);
                            }
                            coursesAdapter = new CoursesAdapter(activity_course_bookmark.this, listOfCourse);
                            coursesAdapter.loadBookmarkedCourses(); // check whether course is bookmarked
                            prepareRecyclerViewCourse(activity_course_bookmark.this, recyclerViewCourse, listOfCourse);
                            coursesAdapter.loadBookmarkedCourses(); // check whether course is bookmarked
                        }
                    }
                });
    }

    public void prepareRecyclerViewCourse(Context context, RecyclerView RV, List<Course> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapterCourse(context, RV, object);
    }

    public void preAdapterCourse(Context context, RecyclerView RV, List<Course> object){
        coursesAdapter = new CoursesAdapter(context, object);
        RV.setAdapter(coursesAdapter);
    }

    public Course convertDocumentToListOfCourse(QueryDocumentSnapshot dc){
        Course course = new Course();
        course.setCourseID(dc.getId());
        course.setCourseTitle(dc.get("title").toString());
        course.setAdvisorID(dc.get("advisorID").toString());
        course.setCourseDesc(dc.get("description").toString());
        course.setCourseLanguage(dc.get("language").toString());
        course.setCourseLevel(dc.get("level").toString());
        course.setCourseMode(dc.get("mode").toString());
        return course;
    }

    private void setUpRVQuiz() {
        quizList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("USER_DETAILS").document(userID).collection("QUIZZES_BOOKMARK");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Quiz> listOfQuiz = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    Quiz topic = convertDocumentToListOfQuiz(dc);
                    listOfQuiz.add(topic);
                }
                quizzesAdapter = new QuizzesAdapter(activity_course_bookmark.this, listOfQuiz);
                quizzesAdapter.loadBookmarkedCourses(); // check whether quiz is bookmarked
                prepareRecyclerView(activity_course_bookmark.this, recyclerViewQuiz, listOfQuiz);
                quizzesAdapter.loadBookmarkedCourses(); // check whether quiz is bookmarked
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Quiz> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Quiz> object){
        quizzesAdapter = new QuizzesAdapter(context, object);
        RV.setAdapter(quizzesAdapter);
    }

    public Quiz convertDocumentToListOfQuiz(QueryDocumentSnapshot dc){
        Quiz quiz = new Quiz();
        quiz.setQuizID(dc.getId());
        quiz.setQuizTitle(dc.get("title").toString());
        quiz.setAdvisorID(dc.get("advisorID").toString());
        quiz.setNumOfQues(dc.get("numOfQues").toString());
        return quiz;
    }

    public void backToPreviousActivity(){
        Intent intent = new Intent(activity_course_bookmark.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}