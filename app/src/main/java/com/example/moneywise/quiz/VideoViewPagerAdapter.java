package com.example.moneywise.quiz;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class VideoViewPagerAdapter extends FragmentPagerAdapter {
    private List<Uri> videoUris;

    public VideoViewPagerAdapter(FragmentManager fm, List<Uri> videoUris) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.videoUris = videoUris;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return VideoFragment.newInstance(videoUris.get(position));
    }

    @Override
    public int getCount() {
        return videoUris.size();
    }
}
