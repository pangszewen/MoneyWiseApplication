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
    ImageButton back;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        videoView = findViewById(R.id.fullScreenVideoView);
        back = findViewById(R.id.backButton);

        if (getIntent() != null && getIntent().hasExtra("videoUri")) {
            String videoUriString = getIntent().getStringExtra("videoUri");
            Uri videoUri = Uri.parse(videoUriString);

            videoView.setVideoURI(videoUri);

            // Set up media controller for full-screen video
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
