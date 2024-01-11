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
    // Variables to store course information
    String courseDesc, courseLevel, courseLanguage, courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        // Initializing UI elements
        AutoCompleteTextView levelSpinner = findViewById(R.id.levelInput);
        AutoCompleteTextView languageSpinner = findViewById(R.id.languageInput);
        Button nextButton = findViewById(R.id.nextButton);
        ImageButton cancelButton = findViewById(R.id.cancelButton);

        // ArrayAdapter for level and language spinners
        ArrayAdapter<CharSequence> adapterLevel = ArrayAdapter.createFromResource(
                getApplicationContext(), R.array.level_array, R.layout.course_dropdown_item);
        ArrayAdapter<CharSequence> adapterLanguage = ArrayAdapter.createFromResource(
                getApplicationContext(), R.array.language_array, R.layout.course_dropdown_item);

        // Setting adapters for spinners
        levelSpinner.setAdapter(adapterLevel);
        languageSpinner.setAdapter(adapterLanguage);

        // Spinner item selection listeners
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed for this example
            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseLanguage = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed for this example
            }
        });

        // Button click listener to proceed to the next step
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieving input values from TextInputEditText
                TextInputEditText title = findViewById(R.id.titleInput);
                TextInputEditText desc = findViewById(R.id.descInput);

                // Assigning input values to variables
                courseTitle = title.getText().toString();
                courseDesc = desc.getText().toString();

                // Validation check before proceeding
                if (!courseTitle.isEmpty() && !courseDesc.isEmpty()
                        && !courseLevel.equals("Please select")
                        && !courseLanguage.equals("Please select")) {
                    // Intent to move to the next activity with course information
                    Intent intent = new Intent(activity_create_course.this, activity_create_lesson.class);
                    intent.putExtra("title", courseTitle);
                    intent.putExtra("desc", courseDesc);
                    intent.putExtra("language", courseLanguage);
                    intent.putExtra("level", courseLevel);
                    intent.putExtra("mode", "Online");
                    startActivity(intent);
                } else {
                    // Snackbar to notify user to fill in all fields
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Please fill in all fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // Button click listener to cancel and return to the previous activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_create_course.this, activity_course_display.class);
                startActivity(intent);
            }
        });
    }
}
