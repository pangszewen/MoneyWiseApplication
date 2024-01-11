package com.example.moneywise.quiz;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneywise.R;

public class VideoFragment extends Fragment {
    private static final String ARG_VIDEO_URI = "videoUri";
    private VideoView videoView;
    private Uri videoUri;

    // Factory method to create a new instance of the fragment
    public static VideoFragment newInstance(Uri videoUri) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_URI, videoUri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the video URI from arguments
        if (getArguments() != null) {
            videoUri = Uri.parse(getArguments().getString(ARG_VIDEO_URI));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        // Initialize the VideoView and set the video URI
        videoView = rootView.findViewById(R.id.videoView);
        videoView.setVideoURI(videoUri);

        // Start playing the video
        videoView.start();

        return rootView;
    }
}
// This fragment is used in activity_create_lesson
