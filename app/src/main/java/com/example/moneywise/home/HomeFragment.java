package com.example.moneywise.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.moneywise.Expenses.BookFragment;
import com.example.moneywise.Expenses.Firebase_Expenses;
import com.example.moneywise.R;
import com.example.moneywise.login_register.ProfileActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    ImageButton profile;
    TextView welcome, budgetTV, expenseTV, differenceTV, monthTV;
    FirebaseAuth auth;
    FirebaseUser user;
    Firebase_Expenses firebaseExpenses = new Firebase_Expenses();
    Timestamp timestamp = Timestamp.now();
    Date currentDate = new Date(timestamp.getSeconds() * 1000); // Convert seconds to milliseconds
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
    String formattedDate = dateFormat.format(currentDate);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        welcome=rootview.findViewById(R.id.TVWelcome);
        budgetTV = rootview.findViewById(R.id.TVAmountBudget);
        expenseTV = rootview.findViewById(R.id.TVAmountExpenses);
        differenceTV = rootview.findViewById(R.id.TVAmountBalance);
        monthTV = rootview.findViewById(R.id.TVMonth);
        profile=rootview.findViewById(R.id.IBProfile);
        setExpensesView();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

        return rootview;
    }

    public void setExpensesView(){
        monthTV.setText(formattedDate);
        firebaseExpenses.getBudget(0, new Firebase_Expenses.BudgetCallback() {
            @Override
            public void onBudgetRetrieved(double budget) {
                // Assign the retrieved budget to the array
                //budget = retrievedBudget;

                double newBudget;

                if (budget % 1 == 0) {
                    // Convert to int if the decimal part is zero
                    newBudget = (int) budget;
                    budgetTV.setText("RM" + Integer.toString((int)newBudget));
                    if (budget==0){
                        budgetTV.setText("Not Set");
                    }
                } else {
                    newBudget = budget;
                    budgetTV.setText("RM" + Double.toString(newBudget));
                }
                firebaseExpenses.calculateTotalExpense(new Firebase_Expenses.TotalExpenseCallback() {
                    double newExpense;
                    @Override
                    public void onTotalExpenseCalculated(double totalExpense) {
                        if (totalExpense % 1 == 0) {
                            // Convert to int if the decimal part is zero
                            newExpense = (int) totalExpense;
                            expenseTV.setText("RM" + Integer.toString((int)newExpense));
                        } else {
                            newExpense = totalExpense;
                            expenseTV.setText("RM" + Double.toString(newExpense));
                        }

                        // Calculate the difference using the array
                        double difference = newBudget - newExpense;

                        if (difference % 1 == 0) {
                            // Convert to int if the decimal part is zero
                            int newDifference = (int) difference;
                            differenceTV.setText("RM" + Integer.toString(newDifference));
                        } else {
                            differenceTV.setText("RM" + Double.toString(difference));
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        // Handle the error, e.g., log it or display a message
                        exception.printStackTrace();
                    }
                });
            }
        });
    }
}