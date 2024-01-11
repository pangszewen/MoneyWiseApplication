package com.example.moneywise.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;

public class activity_quiz_show_result extends AppCompatActivity {
    // Declare variables
    String quizTitle, quizID, numOfQues;
    double score;
    ImageButton cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_show_result);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            quizTitle = intent.getStringExtra("title");
            score = intent.getIntExtra("score", 0);
            quizID = intent.getStringExtra("quizID");
            numOfQues = intent.getStringExtra("numOfQues");
        }

        // Initialize UI elements
        TextView display_text = findViewById(R.id.TVdisplay);
        TextView score_text = findViewById(R.id.score_text);
        ImageView image = findViewById(R.id.image);
        Button retryButton = findViewById(R.id.retryButton);
        cancel = findViewById(R.id.cancelButton);

        // Display appropriate message and image based on the score
        if (score >= 80) {
            display_text.setText("Congratulations!");
            score_text.setText(String.format("Your Score: %.2f%%", score));
            image.setImageResource(R.drawable.course_quiz_high_trophy);
        } else if (score >= 50) {
            display_text.setText("Good Job!");
            score_text.setText(String.format("Your Score: %.2f%%", score));
            image.setImageResource(R.drawable.course_quiz_med_keepitup);
        } else {
            display_text.setText("Don't Give Up!");
            score_text.setText(String.format("Your Score: %.2f%%", score));
            image.setImageResource(R.drawable.course_quiz_low_target);
        }

        // Handle retry button click
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start a new quiz with relevant data
                Intent intent = new Intent(activity_quiz_show_result.this, activity_individual_quiz_page.class);
                intent.putExtra("title", quizTitle);
                intent.putExtra("quizID", quizID);
                intent.putExtra("numOfQues", numOfQues);
                Log.d("title", quizTitle);
                Log.d("quizID", quizID);
                Log.d("numOfQuesInResults", numOfQues);
                startActivity(intent);
            }
        });

        // Handle cancel button click
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the quiz display activity
                Intent intent = new Intent(activity_quiz_show_result.this, activity_quiz_display.class);
                startActivity(intent);
            }
        });
    }
}
