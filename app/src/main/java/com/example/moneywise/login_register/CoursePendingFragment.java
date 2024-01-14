package com.example.moneywise.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.moneywise.R;
import com.example.moneywise.quiz.Course;
import com.example.moneywise.quiz.CoursesCompletedContinueAdapter;
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

public class CoursePendingFragment extends Fragment {
    FirebaseFirestore db;
    String userID;
    private RecyclerView recyclerView;
    private PendingCourseAdapter pendingCourseAdapter;
    List<Course> courseList;
    SwipeRefreshLayout RVCourseRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_pending_display, container, false);
        db = FirebaseFirestore.getInstance();
        RVCourseRefresh = view.findViewById(R.id.RVCourseRefresh);
        recyclerView = view.findViewById(R.id.course_recycle_view);
        setUpRVCourse();
        RVCourseRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVCourse();
                RVCourseRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    // Initialize and retrieve the list of pending courses from Firestore
    public void setUpRVCourse(){
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
                pendingCourseAdapter = new PendingCourseAdapter(getContext(), listOfCourse);
                prepareRecyclerView(getContext(), recyclerView, listOfCourse);
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Course> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Course> object){
        pendingCourseAdapter = new PendingCourseAdapter(context, object);
        RV.setAdapter(pendingCourseAdapter);
    }

    // Convert a Firestore document snapshot to a Course object
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
