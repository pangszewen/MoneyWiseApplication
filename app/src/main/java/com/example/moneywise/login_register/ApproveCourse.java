package com.example.moneywise.login_register;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneywise.R;
import com.example.moneywise.quiz.Course;
import com.example.moneywise.quiz.CourseViewpagerAdapter;
import com.example.moneywise.quiz.fragment_course_lesson_full;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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

public class ApproveCourse extends AppCompatActivity {
    FirebaseFirestore db;
    String courseID, advisorID, userID;
    TextView title, advisor;
    ImageView courseCoverImage;
    Course course = new Course();
    ImageButton backButton;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button approve, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_course);
        db = FirebaseFirestore.getInstance();

        course.setAdvisorID(getIntent().getStringExtra("advisorID"));
        course.setCourseID(getIntent().getStringExtra("courseID"));
        course.setCourseDesc(getIntent().getStringExtra("description"));
        course.setCourseTitle(getIntent().getStringExtra("title"));
        course.setCourseMode(getIntent().getStringExtra("mode"));
        course.setCourseLanguage(getIntent().getStringExtra("language"));
        course.setCourseLevel(getIntent().getStringExtra("level"));
        course.setDateCreated(getIntent().getStringExtra("dateCreated"));
        String previousClass = getIntent().getStringExtra("previousClass");

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
        approve = findViewById(R.id.approveButton);
        reject = findViewById(R.id.rejectButton);

        // One fragment for each tab
        CourseViewpagerAdapter courseViewpagerAdapter = new CourseViewpagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        FragmentDescription fragDesc = new FragmentDescription();
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

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApproveDialog();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRejectDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPreviousActivity();
            }
        });
    }

    //Create a AlertDialog to confirm course approval
    private void showApproveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApproveCourse.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure approve this course?");
        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveApproveStatusToDatabase();
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

    //Save approved course into firebase
    private void saveApproveStatusToDatabase() {
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("advisorID", course.getAdvisorID());
        courseData.put("dateCreated", course.getDateCreated());
        courseData.put("description", course.getCourseDesc());
        courseData.put("language", course.getCourseLanguage());
        courseData.put("level", course.getCourseLevel());
        courseData.put("mode", course.getCourseMode());
        courseData.put("title", course.getCourseTitle());

        DocumentReference courseRef = FirebaseFirestore.getInstance()
                .collection("COURSE")
                .document(course.getCourseID());

        courseRef.set(courseData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Course fields saved successfully!");
                    saveRejectStatusToDatabase();
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Approved!", Snackbar.LENGTH_SHORT).show();
                });
    }

    // Create a AlertDialog to confirm course rejection
    private void showRejectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApproveCourse.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you reject this course?");
        builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveRejectStatusToDatabase();
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

    //Delete the course document from firebase
    private void saveRejectStatusToDatabase() {
        DocumentReference courseRef = FirebaseFirestore.getInstance()
                .collection("COURSE_PENDING")
                .document(course.getCourseID());

        courseRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document with courseId: " + course.getCourseID() + " deleted successfully!");
                    Intent intent = new Intent(ApproveCourse.this, AdministratorModeActivity.class);
                    intent.putExtra("previousClass", ApproveCourse.class.toString());
                    startActivity(intent);
                });
    }

    //Retrieve and display course details from firebase
    private void displayCourse() {
        db.collection("COURSE_PENDING").document(courseID)
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

    // Retrieve and display advisor name from firebase
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

    // Load and display the course cover image from firebase
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

    // Navigate back to the AdministratorModeActivity
    public void backToPreviousActivity() {
        Intent intent = new Intent(ApproveCourse.this, AdministratorModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}