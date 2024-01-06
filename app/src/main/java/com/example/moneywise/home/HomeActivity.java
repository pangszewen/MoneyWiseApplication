package com.example.moneywise.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

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
import com.example.moneywise.forum.Firebase_Forum;
import com.example.moneywise.forum.Forum_MainFragment;
import com.example.moneywise.login_register.AboutAppActivity;
import com.example.moneywise.login_register.Firebase_User;
import com.example.moneywise.login_register.LoginActivity;
import com.example.moneywise.login_register.ProfileActivity;
import com.example.moneywise.login_register.User;
import com.example.moneywise.quiz.fragment_MAIN_CnQ;
import com.example.moneywise.scholarship.ScholarshipMainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FragmentContainerView FCVHome;
    FirebaseAuth auth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.DLHomePage);
        navigationView = findViewById(R.id.nav_side);
        toolbar = findViewById(R.id.myToolBar);
        FCVHome = findViewById(R.id.FCVHome);
        bottomNavigationView = findViewById(R.id.bottomHomeNavigationView);
        bottomNavigationView.setBackground(null);
        replaceFragment(new HomeFragment());


        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if (itemID == R.id.overflowHome) {
                    replaceFragment(new HomeFragment());
                    drawerLayout.closeDrawers(); // Close the drawer after selecting an item
                    return true;
                } else if (itemID == R.id.overflowForum) {
                    replaceFragment(new Forum_MainFragment());
                    bottomNavigationView.findViewById(R.id.iconForum).performClick();
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemID == R.id.overflowExpenses) {
                    replaceFragment(new MainExpensesFragment());
                    bottomNavigationView.findViewById(R.id.iconExpenses).performClick();
                    drawerLayout.closeDrawers();
                    return true;

                }else if(itemID== R.id.overflowCnq){
                    replaceFragment(new fragment_MAIN_CnQ());
                    bottomNavigationView.findViewById(R.id.iconCnq).performClick();
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemID == R.id.overflowSnn) {
                    replaceFragment(new ScholarshipMainFragment());
                    bottomNavigationView.findViewById(R.id.iconSnn).performClick();
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemID == R.id.overflowProfile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemID == R.id.overflowAboutApp) {
                    startActivity(new Intent(HomeActivity.this, AboutAppActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemID == R.id.overflowLogout) {
                    auth.signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });

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
                    replaceFragment(new fragment_MAIN_CnQ());
                    return true;
                }else if(itemID== R.id.iconSnn) {
                    replaceFragment(new ScholarshipMainFragment());
                    return true;
                }else
                    return false;
            }
        });

    }

    public void expensesOnClick(){
        replaceFragment(new MainExpensesFragment());
        bottomNavigationView.findViewById(R.id.iconExpenses).performClick();
        navigationView.findViewById(R.id.overflowExpenses).performClick();
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FCVHome, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}