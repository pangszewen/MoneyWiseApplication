package com.example.moneywise.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class fragment_MAIN_CnQ extends Fragment {
    private RecyclerView recyclerViewCourse, recyclerViewQuiz;
    private NewCoursesAdapter newCoursesAdapter;
    private QuizzesAdapter quizzesAdapter;
    FirebaseFirestore db;
    Button seeAllContinue, seeAllCourse, seeAllQuiz;
    ImageView courseImage;
    TextView courseTitle, authorName;
    String userID;
    List<Course> courseList;
    List<Quiz> quizList;
    ImageButton bookmarkButton;
    ConstraintLayout continueCourse;
    SwipeRefreshLayout swipeRefreshLayout;
    String title, courseID, advisorID, description, level, mode, language;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__m_a_i_n__cn_q, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();

        seeAllContinue = view.findViewById(R.id.BtnSeeAllContinue);
        seeAllCourse = view.findViewById(R.id.BtnSeeAllCourse);
        seeAllQuiz = view.findViewById(R.id.BtnSeeAllQuiz);
        courseImage = view.findViewById(R.id.IVCourseImage);
        courseTitle = view.findViewById(R.id.TVCourseTitle);
        authorName = view.findViewById(R.id.TVAuthorName);
        recyclerViewCourse = view.findViewById(R.id.RVCourse);
        recyclerViewQuiz = view.findViewById(R.id.RVQuiz);
        bookmarkButton = view.findViewById(R.id.bookmarkButton);
        continueCourse = view.findViewById(R.id.CLContinueCourse);
        swipeRefreshLayout = view.findViewById(R.id.RefreshMainPage);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayCompleteCourse();
                setUpRVCourse();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        continueCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call checkContinue and implement the callback
                checkContinue(new CourseCheckCallback() {
                    @Override
                    public void onCourseCheckResult(boolean coursesExist) {
                        // Have courses to continue
                        if (coursesExist) {
                            Intent intent = new Intent(getContext(), activity_individual_course_joined.class);
                            intent.putExtra("courseID", courseID);
                            intent.putExtra("title", title);
                            intent.putExtra("description", description);
                            intent.putExtra("advisorID", advisorID);
                            intent.putExtra("language", language);
                            intent.putExtra("level", level);
                            intent.putExtra("mode", mode);
                            intent.putExtra("previousClass", HomeFragment.class.toString());
                            startActivity(intent);
                        } else { // no courses to continue
                            Toast.makeText(getContext(), "No course to continue!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), activity_course_bookmark.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        seeAllContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getContext(), activity_complete_continue_course.class);
                intent.putExtra("class", getActivity().getClass().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        seeAllCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getContext(), activity_course_display.class);
                intent.putExtra("class", getActivity().getClass().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        seeAllQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getContext(), activity_quiz_display.class);
                intent.putExtra("class", getActivity().getClass().toString());
                startActivity(intent);
            }
        });
        displayCompleteCourse();
        setUpRVCourse();
        setUpRVQuiz();
        return view;
    }

    // check if there are courses to continue
    public interface CourseCheckCallback {
        void onCourseCheckResult(boolean coursesExist);
    }
    private void checkContinue(final CourseCheckCallback callback) {
        CollectionReference courseContinueRef = db.collection("USER_DETAILS")
                .document(userID)
                .collection("COURSES_JOINED");

        courseContinueRef.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean coursesExist = !task.getResult().isEmpty();
                callback.onCourseCheckResult(coursesExist);
            } else {
                callback.onCourseCheckResult(false); // Assume no courses on error
            }
        });
    }

    private void setUpRVQuiz() {
        quizList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("QUIZ");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Quiz> listOfQuiz = new ArrayList<>();
                int count = 0;
                for(QueryDocumentSnapshot dc : task.getResult()){
                    if (count < 3) {
                        Quiz topic = convertDocumentToListOfQuiz(dc);
                        listOfQuiz.add(topic);
                        count++;
                    } else {
                        break;
                    }
                }
                quizzesAdapter = new QuizzesAdapter(getContext(), listOfQuiz);
                quizzesAdapter.loadBookmarkedCourses(); // check is quiz is bookmarked
                prepareRecyclerViewQuiz(getContext(), recyclerViewQuiz, listOfQuiz);
                quizzesAdapter.loadBookmarkedCourses(); // check if quiz is bookmarked
            }
        });
    }

    public void prepareRecyclerViewQuiz(Context context, RecyclerView RV, List<Quiz> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapterQuiz(context, RV, object);
    }

    public void preAdapterQuiz(Context context, RecyclerView RV, List<Quiz> object){
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

    // only display courses that are not join nor completed
    private void setUpRVCourse() {
        courseList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        CollectionReference coursesRef = db.collection("COURSE");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();

        CollectionReference joinedRef = db.collection("USER_DETAILS")
                .document(userID)
                .collection("COURSES_JOINED");

        CollectionReference completedRef = db.collection("USER_DETAILS")
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

                                    if (!isJoined && !isCompleted) { // add to list of course if it is not join or complete
                                        Course course = convertDocumentToListOfCourse(dc);
                                        listOfCourse.add(course);
                                    }
                                }

                                CoursesAdapter coursesAdapter = new CoursesAdapter(getContext(), listOfCourse);
                                coursesAdapter.loadBookmarkedCourses();
                                prepareRecyclerView(getContext(), recyclerViewCourse, listOfCourse);
                                coursesAdapter.loadBookmarkedCourses();
                            }
                        });
                    }
                });
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Course> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Course> object){
        newCoursesAdapter = new NewCoursesAdapter(context, object);
        RV.setAdapter(newCoursesAdapter);
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

    private void displayCompleteCourse() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USER_DETAILS")
                .document(userID)
                .collection("COURSES_JOINED")
                .orderBy("dateJoined", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        title = documentSnapshot.getString("title");
                        courseID = documentSnapshot.getId();
                        advisorID = documentSnapshot.getString("advisorID");
                        description = documentSnapshot.getString("description");
                        level = documentSnapshot.getString("level");
                        mode = documentSnapshot.getString("mode");
                        language = documentSnapshot.getString("language");
                        displayAdvisorName(advisorID);
                        displayCoverImage(courseID);
                        courseTitle.setText(title);
                    }
                });
    }

    private void displayCoverImage(String courseID) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("COURSE_COVER_IMAGE").child(courseID).child("Cover Image"); // Replace with your image file extension
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.course_rectangle_outline_grey) // Replace with a placeholder image while loading
                    .error(R.drawable.course_rectangle_outline_grey) // Replace with an error image if download fails
                    .into(courseImage);
        });
    }
    private void displayAdvisorName(String advisorID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("USER_DETAILS").document(advisorID);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                authorName.setText(name);
            }
        });
    }
}