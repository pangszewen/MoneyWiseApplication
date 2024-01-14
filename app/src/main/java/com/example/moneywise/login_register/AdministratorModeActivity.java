package com.example.moneywise.login_register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.example.moneywise.quiz.CourseViewpagerAdapter;
import com.example.moneywise.quiz.activity_complete_continue_course;
import com.example.moneywise.quiz.fragment_course_completed;
import com.example.moneywise.quiz.fragment_course_continue;
import com.google.android.material.tabs.TabLayout;

public class AdministratorModeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_mode);

        tabLayout = findViewById(R.id.TLAdm);
        viewPager = findViewById(R.id.VPAdm);
        back = findViewById(R.id.backButton);

        // One fragment for each tab
        CourseViewpagerAdapter courseViewpagerAdapter = new CourseViewpagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        CoursePendingFragment fragCP = new CoursePendingFragment();
        QuizPendingFragment fragQP = new QuizPendingFragment();
        courseViewpagerAdapter.addFragment(fragCP, "Course");
        courseViewpagerAdapter.addFragment(fragQP, "Quiz");
        viewPager.setAdapter(courseViewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPreviousActivity();
            }
        });

    }

    // Navigate back to the ProfileActivity
    public void backToPreviousActivity(){
        Intent intent = new Intent(AdministratorModeActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}