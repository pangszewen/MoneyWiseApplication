package com.example.moneywise.quiz;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;
import com.google.android.material.snackbar.Snackbar;

public class FullScreenVideoActivity extends AppCompatActivity {
    private ImageButton back;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        // Find the VideoView and Back button in the layout
        videoView = findViewById(R.id.fullScreenVideoView);
        back = findViewById(R.id.backButton);

        // Check if the intent contains a video URI
        if (getIntent() != null && getIntent().hasExtra("videoUri")) {
            // Get the video URI from the intent
            String videoUriString = getIntent().getStringExtra("videoUri");
            Uri videoUri = Uri.parse(videoUriString);

            // Set the video URI to the VideoView
            videoView.setVideoURI(videoUri);

            // Set up media controller for full-screen video
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }

        // Set a click listener for the back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Go back to the previous screen
            }
        });
    }
}
