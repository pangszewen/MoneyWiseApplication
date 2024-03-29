package com.example.moneywise.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_IndividualTopic_Activity;
import com.example.moneywise.forum.Forum_MyTopic_Activity;
import com.example.moneywise.home.HomeActivity;
import com.google.android.material.tabs.TabLayout;

public class activity_complete_continue_course extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    SearchView searchView;
    ImageButton back;
    String previousClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_continue_course);

        tabLayout = findViewById(R.id.TLCourse);
        viewPager = findViewById(R.id.VPCourse);
        searchView = findViewById(R.id.search);
        back = findViewById(R.id.backButton);

        // One fragment for each tab
        CourseViewpagerAdapter courseViewpagerAdapter = new CourseViewpagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragment_course_continue fragCon = new fragment_course_continue(); // Continue course fragment
        fragment_course_completed fragComp = new fragment_course_completed(); // Completed Course fragment
        courseViewpagerAdapter.addFragment(fragCon, "Continue");
        courseViewpagerAdapter.addFragment(fragComp, "Completed");
        viewPager.setAdapter(courseViewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // get the previous class users accessed
        previousClass = getIntent().getStringExtra("previousClass");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // perform search
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                fragCon.onSearch(s); // search in continue tab
                fragComp.onSearch(s); // search in completed tab
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPreviousActivity();
            }
        });

    }
    // when back button is pressed
    public void backToPreviousActivity(){
        Intent intent = new Intent(activity_complete_continue_course.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}