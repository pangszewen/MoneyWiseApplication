package com.example.moneywise.login_register;

import android.graphics.text.LineBreaker;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneywise.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentDescription extends Fragment {
    FirebaseFirestore db;
    String courseID;
    TextView desc, language, mode, level;
    String previousClass;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_description, container, false);

        desc = view.findViewById(R.id.TVDescription);
        level = view.findViewById(R.id.TVLevel);
        language = view.findViewById(R.id.TVLanguage);
        mode = view.findViewById(R.id.TVMode);

        Bundle bundle = getArguments();
        if (bundle != null) {
            courseID = bundle.getString("courseID");
            previousClass = bundle.getString("previousClass");
        }
        displayPending();

        return view;
    }

    // Fetch and display pending course details from Firestore
    private void displayPending() {
        db = FirebaseFirestore.getInstance();
        db.collection("COURSE_PENDING").document(courseID)
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