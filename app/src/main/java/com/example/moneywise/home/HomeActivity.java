package com.example.moneywise.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneywise.Expenses.MainExpensesFragment;
import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_MainFragment;
import com.example.moneywise.scholarship.ScholarshipMainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FragmentContainerView FCVHome;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = findViewById(R.id.DLHomePage);
        navigationView = findViewById(R.id.nav_side);
        toolbar = findViewById(R.id.myToolBar);
        FCVHome = findViewById(R.id.FCVHome);
        bottomNavigationView = findViewById(R.id.bottomHomeNavigationView);
        bottomNavigationView.setBackground(null);
        replaceFragment(new HomeFragment());

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        //need set welcome text

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if(itemID== R.id.iconHome) {
                    replaceFragment(new HomeFragment());
                    return true;
                }else if(itemID== R.id.iconForum) {
                    replaceFragment(new Forum_MainFragment());
                    return true;
                }else if(itemID== R.id.iconExpenses) {
                    replaceFragment(new MainExpensesFragment());
                    return true;
                }else if(itemID== R.id.iconCnq){
                    //startActivity(new Intent(HomeActivity.this, activity_course_display.class));
                    return true;
                }else if(itemID== R.id.iconSnn) {
                    replaceFragment(new ScholarshipMainFragment());
                    return true;
                }else
                    return false;
            }
        });

    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FCVHome, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}