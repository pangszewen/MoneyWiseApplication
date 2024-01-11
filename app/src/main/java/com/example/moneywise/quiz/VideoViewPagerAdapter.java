package com.example.moneywise.quiz;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class VideoViewPagerAdapter extends FragmentPagerAdapter {
    private List<Uri> videoUris;

    // Constructor for the adapter, takes FragmentManager and a list of video URIs
    public VideoViewPagerAdapter(FragmentManager fm, List<Uri> videoUris) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.videoUris = videoUris;
    }

    // Return a new instance of VideoFragment for the specified position
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return VideoFragment.newInstance(videoUris.get(position));
    }

    // Return the total number of items in the adapter, representing the number of videos
    @Override
    public int getCount() {
        return videoUris.size();
    }
}
// This adapter is used in activity_create_lesson
