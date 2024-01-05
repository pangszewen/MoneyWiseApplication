package com.example.moneywise.quiz;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_IndividualTopic_Activity;
import com.example.moneywise.forum.Forum_MyTopic_Activity;
import com.example.moneywise.home.HomeActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class activity_individual_course_joined extends AppCompatActivity {
    FirebaseFirestore db;
    String courseID, advisorID, userID;
    TextView title, advisor;
    ImageView courseCoverImage;
    Course course = new Course();
    ImageButton backButton;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button completeCourse;
    String previousClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_course_joined);
        db = FirebaseFirestore.getInstance();

        course.setAdvisorID(getIntent().getStringExtra("advisorID"));
        course.setCourseID(getIntent().getStringExtra("courseID"));
        course.setCourseDesc(getIntent().getStringExtra("description"));
        course.setCourseTitle(getIntent().getStringExtra("title"));
        course.setCourseMode(getIntent().getStringExtra("mode"));
        course.setCourseLanguage(getIntent().getStringExtra("language"));
        course.setCourseLevel(getIntent().getStringExtra("level"));
        previousClass = getIntent().getStringExtra("previousClass");

        courseID = course.getCourseID();
        Bundle bundle = new Bundle();
        bundle.putString("advisorID", course.getAdvisorID());
        bundle.putString("description", course.getCourseDesc());
        bundle.putString("level", course.getCourseLevel());
        bundle.putString("mode", course.getCourseMode());
        bundle.putString("language", course.getCourseLanguage());
        bundle.putString("courseID", course.getCourseID());
        bundle.putString("previousClass", previousClass);

        courseCoverImage = findViewById(R.id.courseImage);
        title = findViewById(R.id.TVCourseTitle);
        advisor = findViewById(R.id.TVAdvisorName);
        backButton = findViewById(R.id.backButton);
        tabLayout = findViewById(R.id.TLCourse);
        viewPager = findViewById(R.id.VPCourse);
        completeCourse = findViewById(R.id.completeCourseButton);

        CourseViewpagerAdapter courseViewpagerAdapter = new CourseViewpagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragment_course_desc fragDesc = new fragment_course_desc();
        fragDesc.setArguments(bundle);
        fragment_course_lesson_full fragLessonFull = new fragment_course_lesson_full();
        fragLessonFull.setArguments(bundle);
        courseViewpagerAdapter.addFragment(fragDesc, "Description");
        courseViewpagerAdapter.addFragment(fragLessonFull, "Lessons");
        viewPager.setAdapter(courseViewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        displayCourse();
        
        checkCourse(userID, courseID);
        completeCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPreviousActivity();
            }
        });
    }

    private void checkCourse(String userId, String courseId) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("USER_DETAILS")
                .document(userId).collection("COURSES_COMPLETED").document(courseId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    processCourseCompletion(true);
                } else {
                    processCourseCompletion(false);
                }
            }
        });
    }

    private void processCourseCompletion(boolean courseCompleted) {
        if (courseCompleted) {
            completeCourse.setText("COMPLETED");
            completeCourse.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
    }



    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_individual_course_joined.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you completed this course?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeStatusFromComplete();
                saveStatusToDatabase();
                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, "Completed Course!", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(activity_individual_course_joined.this, activity_complete_continue_course.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeStatusFromComplete() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USER_DETAILS")
                .document(userID)
                .collection("COURSES_JOINED")
                .document(course.getCourseID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                   Log.d("TAG", "Removed from joined course");
                });
    }

    private void saveStatusToDatabase() {
        Map<String, Object> courseData = new HashMap<>();
        Timestamp currentTime = Timestamp.now();
        Log.d("title", course.getCourseTitle());
        courseData.put("title", course.getCourseTitle());
        courseData.put("advisorID", course.getAdvisorID());
        courseData.put("dateCompleted", currentTime);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("USER_DETAILS").document(userID);
        userRef.collection("COURSES_COMPLETED").document(course.getCourseID()).set(courseData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Course ID added to completed");
                });
    }

    private void displayCourse() {
        db.collection("COURSE").document(courseID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String titleText = document.getString("title");
                            advisorID = document.getString("advisorID");
                            title.setText(titleText);

                            displayAdvisorName(advisorID);
                            displayCoverImage();
                        }
                    }
                });
    }

    private void displayAdvisorName(String advisorID) {
        db.collection("USER_DETAILS").document(advisorID)
                .get()
                .addOnCompleteListener(advisorTask -> {
                    if (advisorTask.isSuccessful() && advisorTask.getResult() != null) {
                        DocumentSnapshot advisorDocument = advisorTask.getResult();
                        if (advisorDocument.exists()) {
                            String advisorName = advisorDocument.getString("name");
                            advisor.setText(advisorName);
                        }
                    }
                });
    }

    private void displayCoverImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("COURSE_COVER_IMAGE/" + courseID + "/");
        storageReference.child("Cover Image").getDownloadUrl().addOnSuccessListener(uri -> { // Need remove jpeg
            String imageUri = uri.toString();
            Picasso.get().load(imageUri).into(courseCoverImage);
            Log.d("Cover Image", "Successful");
        }).addOnFailureListener(exception -> {
        });
    }

    public void backToPreviousActivity(){
        Intent intent = new Intent(activity_individual_course_joined.this, activity_complete_continue_course.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}