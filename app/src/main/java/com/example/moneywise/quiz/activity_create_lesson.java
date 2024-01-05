package com.example.moneywise.quiz;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class activity_create_lesson extends AppCompatActivity {
    Boolean save = false;
    String courseID, courseTitle, courseDesc, courseLevel, courseMode, courseLanguage;
    ViewPager viewPagerImage;
    Uri imageUri;
    Uri videoUri;
    ViewPager viewPagerVideo;
    ArrayList<Uri> chooseImageList;
    ArrayList<Uri> chooseVideoList;
    FirebaseStorage storage;;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        Intent intent = getIntent();
        chooseImageList = new ArrayList<>();
        chooseVideoList = new ArrayList<>();

        // fetch data from previous activity
        if (intent != null){
            courseTitle = intent.getStringExtra("title");
            courseDesc = intent.getStringExtra("desc");
            courseLevel = intent.getStringExtra("level");
            courseMode = intent.getStringExtra("mode");
            courseLanguage = intent.getStringExtra("language");
        }

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!save && !chooseImageList.isEmpty() && !chooseVideoList.isEmpty()) { // will check if all fields are filled
                    createCourse(courseTitle, courseDesc, courseLevel, courseLanguage, courseMode);
                    Intent intent = new Intent(activity_create_lesson.this, activity_course_display.class);
                    startActivity(intent);
                }
                else if (save) {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Saved!", Snackbar.LENGTH_SHORT).show();
                } else if (!save && chooseImageList.isEmpty()) { // no upload cover image
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Please select a cover image!", Snackbar.LENGTH_SHORT).show();
                } else if (!save && chooseVideoList.isEmpty()) { // no upload any lessons
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Please select videos to upload!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        viewPagerImage = findViewById(R.id.viewPagerImage);
        viewPagerImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        viewPagerVideo = findViewById(R.id.viewPagerVideo);
        Button addCoverImage = findViewById(R.id.addCoverImage);
        addCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                pickImageFromGallery();
            }
        });

        Button addLesson = findViewById(R.id.addVideo);
        addLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                pickVideosFromGallery();
            }
        });
    }

    // Select videos for lesson
    private void pickVideosFromGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"), 1);
    }

    // Select image as cover image
    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) { // For videos
                handleVideoSelection(data);
            }
            if (requestCode == 2) { // For images
                handleImageSelection(data);
            }
        }
    }

    private void handleVideoSelection(Intent data) {
        if (data.getClipData() != null) {
            int itemCount = data.getClipData().getItemCount();
            for (int i = 0; i < itemCount; i++) {
                videoUri = data.getClipData().getItemAt(i).getUri();
                chooseVideoList.add(videoUri);
                LayoutInflater layoutInflater = getLayoutInflater();
                VideoViewPagerAdapter videoPagerAdapter = new VideoViewPagerAdapter(getSupportFragmentManager(), chooseVideoList);
                viewPagerVideo.setAdapter(videoPagerAdapter);
            }
        }
    }
    private void handleImageSelection(Intent data) {
        if (data.getClipData() != null) {
            int itemCount = data.getClipData().getItemCount();
            for (int i = 0; i < itemCount; i++) {
                imageUri = data.getClipData().getItemAt(i).getUri();
                chooseImageList.add(imageUri);
            }
            setImageViewPagerAdapter();
        }
    }

    private void setImageViewPagerAdapter() {
        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, chooseImageList);
        viewPagerImage.setAdapter(adapter);
    }

    // check courses saved in database
    private void createCourse(String title, String description, String level, String language, String mode) {
        Log.d("TAG", "CreateCourse");
        CollectionReference collectionReference = db.collection("COURSE");
        collectionReference.orderBy("dateCreated", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Course> courseList = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()) {
                    Course course = convertDocumentToCourse(dc);
                    courseList.add(course);
                }
                courseID = generateCourseID(courseList);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                String advisorID = user.getUid();
                Course newCourse = new Course(courseID, advisorID, title, description, level, language, mode);
                newCourse.setCoverImageUri(imageUri);
                insertTopicIntoDatabase(newCourse);
                uploadImages(newCourse.getCourseID());
                uploadLessons(newCourse.getCourseID());
            }
        });
    }

    // upload lesson to storage and name as Lesson.. based on upload sequence under courseID
    private void uploadLessons(String courseID) {
        int i = 1;
        for (Uri videoUri : chooseVideoList) {
            String filename = "lesson " + i;
            StorageReference storageRef = storage.getReference().child("COURSE_LESSONS").child(courseID).child(filename);
            storageRef.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.e(TAG, "Successfully Uploaded Lessons");
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to upload video: " + exception.getMessage());
                    });
            i++;
        }
    }

    // upload cover image to storage and name as Cover Image under courseID
    private void uploadImages(String courseID){
        for(int i =0; i<chooseImageList.size(); i++){
            Uri image = chooseImageList.get(i);
            if(image!=null){
                StorageReference reference = storage.getReference().child("COURSE_COVER_IMAGE").child(courseID);
                StorageReference imageName = reference.child("Cover Image");
                imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
                });
            }
        }
    }

    // insert course to database
    private void insertTopicIntoDatabase(Course course) {
        Map<String, Object> map = new HashMap<>();
        map.put("advisorID", course.getAdvisorID());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = course.getDateCreated().format(formatter);
        map.put("dateCreated", formattedDateTime);
        map.put("title", course.getCourseTitle());
        map.put("description", course.getCourseDesc());
        map.put("level", course.getCourseLevel());
        map.put("language", course.getCourseLanguage());
        map.put("mode", course.getCourseMode());
        db.collection("COURSE_PENDING").document(course.getCourseID()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    save = true;
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Course Created!", Snackbar.LENGTH_SHORT).show();

                }else {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Failed to Create Course", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Course convertDocumentToCourse(QueryDocumentSnapshot dc){
        Course course = new Course();
        course.setCourseID(dc.getId());
        course.setAdvisorID(dc.get("advisorID").toString());
        course.setDateCreated(dc.get("dateCreated").toString());
        course.setCourseTitle(dc.get("title").toString());
        course.setCourseDesc(dc.get("description").toString());
        course.setCourseLevel(dc.get("level").toString());
        course.setCourseLanguage(dc.get("language").toString());
        course.setCourseMode(dc.get("mode").toString());
        return course;
    }

    // create course ID C0001000
    private String generateCourseID(ArrayList<Course> courses){
        String newID;
        Random rand = new Random();
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = "C" + String.format("%07d", randomNum);
            if(checkDuplicatedTopicID(newID, courses))
                break;
        }
        Log.d("TAG", "This is new courseID " + newID);
        return newID;
    }

    // check for duplication
    private boolean checkDuplicatedTopicID(String newID, ArrayList<Course> courses){
        for(Course topic: courses){
            if(newID.equals(topic.getCourseID()))
                return false;
        }
        Log.d("TAG", "This is checked topic ID " + newID);
        return true;
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int check = ContextCompat.checkSelfPermission(activity_create_lesson.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(check!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity_create_lesson.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }else{
                pickImageFromGallery();
            }
        }else{
            pickImageFromGallery();
        }
    }
}