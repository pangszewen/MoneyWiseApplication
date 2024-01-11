package com.example.moneywise.quiz;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.example.moneywise.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class fragment_course_lesson_single extends Fragment {
    private VideoView lessonVid;
    private TextView lessonText, lessonTime;
    private String courseID;
    private int totalDuration = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_lesson_single, container, false);

        // Find views in the layout
        lessonVid = view.findViewById(R.id.VVLesson);
        lessonText = view.findViewById(R.id.TVLessonTitle);
        lessonTime = view.findViewById(R.id.TVCourseTime);

        // Get course ID from the arguments
        Bundle bundle = getArguments();
        if (bundle != null)
            courseID = bundle.getString("courseID");

        // Firebase Storage setup
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("COURSE_LESSONS/" + courseID + "/lesson 1");

        try {
            // Create a temporary file to store the video
            final File localFile = File.createTempFile("lesson 1", "mp4");

            // Download the video file from Firebase Storage to the local file
            storageRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Set the video URI for the VideoView
                        lessonVid.setVideoURI(Uri.fromFile(localFile));

                        // Set up the listener to get the video duration when prepared
                        lessonVid.setOnPreparedListener(mp -> {
                            totalDuration = lessonVid.getDuration();
                            int durationSeconds = totalDuration / 1000;
                            int minutes = durationSeconds / 60;
                            int seconds = durationSeconds % 60;
                            lessonTime.setText(String.format("%dm %ds", minutes, seconds));
                        });

                        // Request focus and start playing the video
                        lessonVid.requestFocus();
                        lessonVid.start();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
}
