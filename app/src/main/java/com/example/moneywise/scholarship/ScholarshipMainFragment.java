package com.example.moneywise.scholarship;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_MyTopic_Activity;

public class ScholarshipMainFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_scholarship_main, container, false);

        // To FindScholarship
        AppCompatButton btnFindMore = rootview.findViewById(R.id.btnFindMore);

        btnFindMore.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), FindScholarshipActivity.class));

        });

        // To FindNews
        AppCompatButton btnSeeMore = rootview.findViewById(R.id.btnSeeMore);

        btnSeeMore.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), FindNewsActivity.class));
        });

        // To Bookmark
        ImageView scholarshipMain = rootview.findViewById(R.id.imageSave);
        scholarshipMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BookmarkActivity.class));
            }
        });

        AppCompatButton btnBookMark = rootview.findViewById(R.id.btnBookMark);

        btnBookMark.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), BookmarkActivity.class));
        });

        return rootview;
    }
}