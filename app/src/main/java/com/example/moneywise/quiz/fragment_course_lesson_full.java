package com.example.moneywise.quiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_course_lesson_full extends Fragment {
    private RecyclerView recyclerView;
    private LessonsAdapter lessonsAdapter;
    FirebaseFirestore db;
    String courseID;
    List<Lesson> lessonList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_lesson_full, container, false);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.RVLessons);

        // Check if receive bundle
        Bundle bundle = getArguments();
        if (bundle != null)
            courseID = bundle.getString("courseID");
        setUpRVLesson();
        return view;
    }

    public void setUpRVLesson() {
        lessonList = new ArrayList<>();
        retrieveVideoUrlsForLessons();
    }

    private void retrieveVideoUrlsForLessons() {
        lessonList = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("COURSE_LESSONS").child(courseID);
        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    Map<Integer, Lesson> lessonMap = new HashMap<>(); // Map to store lessons by number

                    for (StorageReference item : listResult.getItems()) {
                        String itemName = item.getName();
                        int lessonNumber = extractLessonNumber(itemName); // Extract lesson number from the item name

                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            Lesson lesson = new Lesson("Lesson " + lessonNumber, "", uri.toString());
                            lessonMap.put(lessonNumber, lesson);

                            if (lessonMap.size() == listResult.getItems().size()) {
                                updateAdapterFromMap(lessonMap);
                            }
                        }).addOnFailureListener(exception -> {
                            Log.d("Lesson URL", "Failed to retrieve");
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Lesson URL", "Failed to retrieve");
                });
    }

    // Extract lesson number from item list
    private int extractLessonNumber(String itemName) {
        return Integer.parseInt(itemName.replaceAll("[^0-9]", ""));
    }

    // Updates the adapter with lessons sorted by lesson number
    private void updateAdapterFromMap(Map<Integer, Lesson> lessonMap) {
        List<Lesson> sortedLessons = new ArrayList<>(lessonMap.values());
        Collections.sort(sortedLessons, Comparator.comparingInt(o -> extractLessonNumber(o.getLessonTitle())));

        lessonList.clear();
        lessonList.addAll(sortedLessons);
        setAdapter();
    }

    private void setAdapter() {
        Context context = getContext();
        if (context != null) {
            lessonsAdapter = new LessonsAdapter(context, lessonList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(lessonsAdapter);
        }
    }
}

