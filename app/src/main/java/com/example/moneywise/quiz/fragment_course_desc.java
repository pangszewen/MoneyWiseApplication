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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_desc, container, false);
        
        desc = view.findViewById(R.id.TVDescription);
        level = view.findViewById(R.id.TVLevel);
        language = view.findViewById(R.id.TVLanguage);
        mode = view.findViewById(R.id.TVMode);

        Bundle bundle = getArguments();
        if (bundle != null)
            courseID = bundle.getString("courseID");
        else
            System.out.println("NULLLLLLLLLLL");
        displayDesc();

        return view;
    }

    private void displayDesc() {
        db = FirebaseFirestore.getInstance();
        db.collection("COURSE").document(courseID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String descText = document.getString("description");
                            String leveltext = document.getString("level");
                            String languageText = document.getString("language");
                            String modeText = document.getString("mode");
                            desc.setText(descText);
                            level.setText(leveltext);
                            language.setText(languageText);
                            mode.setText(modeText);
                            desc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                        }
                    }
                });
    }
}