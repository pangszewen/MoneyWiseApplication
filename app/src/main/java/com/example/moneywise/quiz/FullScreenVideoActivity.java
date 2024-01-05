package com.example.moneywise.quiz;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.moneywise.R;

public class FullScreenVideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        videoView = findViewById(R.id.fullScreenVideoView);

        if (getIntent() != null && getIntent().hasExtra("videoUri")) {
            String videoUriString = getIntent().getStringExtra("videoUri");
            Uri videoUri = Uri.parse(videoUriString);

            videoView.setVideoURI(videoUri);

            // Set up media controller for full-screen video
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }
    }
}
