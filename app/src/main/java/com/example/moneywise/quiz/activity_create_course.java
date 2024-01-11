package com.example.moneywise.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class activity_create_course extends AppCompatActivity {
    String courseDesc, courseLevel, courseLanguage, courseTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        AutoCompleteTextView levelSpinner = findViewById(R.id.levelInput);
        AutoCompleteTextView languageSpinner = findViewById(R.id.languageInput);
        Button nextButton = findViewById(R.id.nextButton);
        ImageButton cancelButton = findViewById(R.id.cancelButton);

        ArrayAdapter<CharSequence> adapterLevel = ArrayAdapter.createFromResource(getApplicationContext(), R.array.level_array,R.layout.course_dropdown_item);
        ArrayAdapter<CharSequence> adapterLanguage = ArrayAdapter.createFromResource(getApplicationContext(), R.array.language_array, R.layout.course_dropdown_item);
        levelSpinner.setAdapter(adapterLevel);
        languageSpinner.setAdapter(adapterLanguage);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseLevel = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseLanguage = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText title = findViewById(R.id.titleInput);
                TextInputEditText desc = findViewById(R.id.descInput);

                courseTitle = title.getText().toString();
                courseDesc = desc.getText().toString();
                courseLevel = levelSpinner.getText().toString(); // Updated here
                courseLanguage = languageSpinner.getText().toString(); // Updated here

                if (!courseTitle.isEmpty() && !courseDesc.isEmpty() && !courseLevel.equals("Please select") && !courseLanguage.equals("Please select")) { // check if all fields are filled
                    Intent intent = new Intent(activity_create_course.this, activity_create_lesson.class);
                    intent.putExtra("title", courseTitle);
                    intent.putExtra("desc", courseDesc);
                    intent.putExtra("language", courseLanguage);
                    intent.putExtra("level", courseLevel);
                    intent.putExtra("mode", "Online");
                    startActivity(intent);
                }
                else { // Incomplete info
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Please fill in all fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_create_course.this, activity_course_display.class);
                startActivity(intent);
            }
        });
    }
}
