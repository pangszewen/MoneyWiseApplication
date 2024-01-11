package com.example.moneywise.quiz;

import android.graphics.text.LineBreaker;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.moneywise.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragment_course_desc extends Fragment {
    FirebaseFirestore db;
    String courseID;
    TextView desc, language, mode, level;
    String previousClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_desc, container, false);

        // Initialize TextViews
        desc = view.findViewById(R.id.TVDescription);
        level = view.findViewById(R.id.TVLevel);
        language = view.findViewById(R.id.TVLanguage);
        mode = view.findViewById(R.id.TVMode);

        // Get data from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            courseID = bundle.getString("courseID");
            previousClass = bundle.getString("previousClass");
        }

        // Display course information
        displayDesc();

        return view;
    }

    // Display information for pending courses
    private void displayPending() {
        db = FirebaseFirestore.getInstance();
        db.collection("COURSE_PENDING").document(courseID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get information from the document
                            String descText = document.getString("description");
                            String leveltext = document.getString("level");
                            String languageText = document.getString("language");
                            String modeText = document.getString("mode");

                            // Set TextViews with the obtained information
                            desc.setText(descText);
                            level.setText(leveltext);
                            language.setText(languageText);
                            mode.setText(modeText);

                            // Apply justification for multiline TextViews
                            desc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                        }
                    }
                });
    }

    // Display information for approved courses
    private void displayDesc() {
        db = FirebaseFirestore.getInstance();
        db.collection("COURSE").document(courseID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get information from the document
                            String descText = document.getString("description");
                            String leveltext = document.getString("level");
                            String languageText = document.getString("language");
                            String modeText = document.getString("mode");

                            // Set TextViews with the obtained information
                            desc.setText(descText);
                            level.setText(leveltext);
                            language.setText(languageText);
                            mode.setText(modeText);

                            // Apply justification for multiline TextViews
                            desc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                        }
                    }
                });
    }
}
