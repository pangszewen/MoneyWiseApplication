package com.example.moneywise.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class activity_course_display extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CoursesAdapter coursesAdapter;
    FirebaseFirestore db;
    List<Course> courseList;
    FloatingActionButton createCourse;
    SwipeRefreshLayout RVCourseRefresh;
    ImageButton backButton;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_display);
        db = FirebaseFirestore.getInstance();
        RVCourseRefresh = findViewById(R.id.RVCourseRefresh);
        recyclerView = findViewById(R.id.course_recycle_view);
        createCourse = findViewById(R.id.createCourseButton);
        backButton = findViewById(R.id.backButton);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        setUpRVCourse();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        isAdvisor(userID, isAdvisor -> { // check if user is advisor
            if (!isAdvisor)
                createCourse.setVisibility(View.GONE); // if not advisor, cannot create course
        });
        createCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_course_display.this, activity_create_course.class);
                startActivity(intent);
            }
        });

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
                backToPreviousActivity();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // perform search
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                coursesAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    // Check if user is advisor
    public interface AdvisorCheckCallback {
        void onRoleChecked(boolean isAdvisor);
    }

    private void isAdvisor(String userID, AdvisorCheckCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("USER_DETAILS").document(userID);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if (role != null && role.equals("Advisor")) {
                    callback.onRoleChecked(true);
                } else {
                    callback.onRoleChecked(false);
                }
            } else {
                callback.onRoleChecked(false);
            }
        }).addOnFailureListener(e -> {
            callback.onRoleChecked(false);
        });
    }

    // will check if the course is completed/join, if yes, then will not display in course list
    public void setUpRVCourse() {
        courseList = new ArrayList<>();
        CollectionReference coursesRef = db.collection("COURSE");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        CollectionReference joinedRef = db.collection("USER_DETAILS")// refer to course joined
                .document(userID)
                .collection("COURSES_JOINED");

        CollectionReference completedRef = db.collection("USER_DETAILS")// refer to course completed
                .document(userID)
                .collection("COURSES_COMPLETED");

        coursesRef.get().addOnCompleteListener(coursesTask -> {
            if (coursesTask.isSuccessful()) {
                joinedRef.get().addOnCompleteListener(joinedTask -> {
                    if (joinedTask.isSuccessful()) {
                        completedRef.get().addOnCompleteListener(completedTask -> {
                            if (completedTask.isSuccessful()) {
                                List<Course> listOfCourse = new ArrayList<>();
                                for (QueryDocumentSnapshot dc : coursesTask.getResult()) {
                                    String courseId = dc.getId();
                                    boolean isJoined = false;
                                    boolean isCompleted = false;

                                    for (DocumentSnapshot joinedSnapshot : joinedTask.getResult()) {
                                        if (joinedSnapshot.getId().equals(courseId)) {
                                            isJoined = true;
                                            break;
                                        }
                                    }

                                    for (DocumentSnapshot completedSnapshot : completedTask.getResult()) {
                                        if (completedSnapshot.getId().equals(courseId)) {
                                            isCompleted = true;
                                            break;
                                        }
                                    }

                                    if (!isJoined && !isCompleted) { // if course is not joined/complete will add to list of course to display
                                        Course course = convertDocumentToListOfCourse(dc);
                                        listOfCourse.add(course);
                                    }
                                }
                                coursesAdapter = new CoursesAdapter(activity_course_display.this, listOfCourse);
                                coursesAdapter.loadBookmarkedCourses(); // check course is bookmarked
                                prepareRecyclerView(activity_course_display.this, recyclerView, listOfCourse);
                                coursesAdapter.loadBookmarkedCourses(); // check if course is bookmarked
                            }
                        });
                    }
                });
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
        }
        return course;
    }

    public void backToPreviousActivity(){
        Intent intent = new Intent(activity_course_display.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
