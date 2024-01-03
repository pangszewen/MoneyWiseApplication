package com.example.moneywise.Expenses;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneywise.R;
import com.example.moneywise.databinding.FragmentMainExpensesBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainExpensesFragment extends Fragment {

    private FragmentMainExpensesBinding binding;
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainExpensesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        replaceFragment(new BookFragment());

        binding.bottomNavigationViewExpenses.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.book) {
                replaceFragment(new BookFragment());
            } else if (item.getItemId() == R.id.analytics) {
                replaceFragment(new AnalyticsFragment());
            } else if (item.getItemId() == R.id.budget) {
                replaceFragment(new BudgetFragment());
            } else if (item.getItemId() == R.id.trend) {
                replaceFragment(new TrendFragment());
            }
            return true;
        });

        // Disable default icon tinting
        binding.bottomNavigationViewExpenses.setItemIconTintList(null);
        binding.bottomNavigationViewExpenses.setItemTextColor(null);

        // Set custom icon tinting
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked}, // checked
                new int[]{-android.R.attr.state_checked}  // unchecked
        };

        int selectedIconColor = ContextCompat.getColor(requireContext(), R.color.white);
        int unselectedIconColor = ContextCompat.getColor(requireContext(), R.color.dark_blue);
        int selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white);
        int unselectedTextColor = ContextCompat.getColor(requireContext(), R.color.dark_blue);

        int[] colors = new int[]{
                selectedIconColor,
                unselectedIconColor
        };

        int[] textColors = new int[]{
                selectedTextColor,
                unselectedTextColor
        };

        ColorStateList iconColorStateList = new ColorStateList(states, colors);
        ColorStateList textColorStateList = new ColorStateList(states, textColors);

        binding.bottomNavigationViewExpenses.setItemIconTintList(iconColorStateList);
        binding.bottomNavigationViewExpenses.setItemTextColor(textColorStateList);

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
