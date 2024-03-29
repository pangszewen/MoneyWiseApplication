package com.example.moneywise.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneywise.expenses.MainExpensesFragment;
import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_MainFragment;
import com.example.moneywise.login_register.AboutAppActivity;
import com.example.moneywise.login_register.LoginActivity;
import com.example.moneywise.quiz.fragment_MAIN_CnQ;
import com.example.moneywise.scholarship.ScholarshipMainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

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
        bottomNavigationView.getMenu().getItem(2).setTitle("Learning");
        bottomNavigationView.getMenu().getItem(3).setTitle("Info");
        bottomNavigationView.setBackground(null);
        replaceFragment(new HomeFragment());

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        navigationView.setNavigationItemSelectedListener(item -> {
            handleMenuItemSelection(item);
            bottomNavigationView.setSelectedItemId(item.getItemId());
            return true;
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleMenuItemSelection(item);
            navigationView.setCheckedItem(item.getItemId());
            return true;
        });


    }

    // Allow synchronization of side nav and bottom nav
    private void handleMenuItemSelection(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.iconHome)
            replaceFragment(new HomeFragment());
        else if(itemID == R.id.iconExpenses)
            replaceFragment(new MainExpensesFragment());
        else if(itemID == R.id.iconCnq)
            replaceFragment(new fragment_MAIN_CnQ());
        else if(itemID == R.id.iconSnn)
            replaceFragment(new ScholarshipMainFragment());
        else if(itemID == R.id.iconForum)
            replaceFragment(new Forum_MainFragment());
        else if(itemID == R.id.iconAboutApp)
            startActivity(new Intent(HomeActivity.this, AboutAppActivity.class));
        else if(itemID == R.id.iconLogout) {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
        drawerLayout.closeDrawers();
    }

    public void expensesOnClick(){
        replaceFragment(new MainExpensesFragment());
        bottomNavigationView.findViewById(R.id.iconExpenses).performClick();
        navigationView.findViewById(R.id.iconExpenses).performClick();
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FCVHome, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}