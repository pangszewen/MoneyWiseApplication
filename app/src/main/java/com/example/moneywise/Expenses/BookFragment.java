package com.example.moneywise.Expenses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.R;
import com.example.moneywise.databinding.FragmentBookBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment {

    private FragmentBookBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout RVRefreshExpense;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList = new ArrayList<>(); // Replace with your data type

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();
    Timestamp timestamp = Timestamp.now();
    Date currentDate = new Date(timestamp.getSeconds() * 1000); // Convert seconds to milliseconds
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
    String formattedDate = dateFormat.format(currentDate);
    // Access or create the user's document in "USER_DETAILS" collection
    DocumentReference userDocRef = db.collection("USER_DETAILS").document(userID);

    // Access or create the "expenses" subcollection for the current user
    CollectionReference expensesCollection = userDocRef.collection("EXPENSE");

    // Access or create the document for the current month and year
    DocumentReference monthDocRef = expensesCollection.document("EXPENSE_MONTH");

    // Access or create the "expense" subcollection for the current month
    CollectionReference monthExpensesCollection = monthDocRef.collection(formattedDate);
    private static final int CALCULATOR_REQUEST_CODE = 1;
    Firebase_Expenses fbe = new Firebase_Expenses();
    DocumentReference budgetMonthDocRef = expensesCollection.document("EXPENSE_BUDGET_MONTH");
    CollectionReference monthBudgetCollection = budgetMonthDocRef.collection(formattedDate);

    public BookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookFragment newInstance(String param1, String param2) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment using View Binding
        View rootView = inflater.inflate(R.layout.fragment_book, container, false);

        recyclerView = rootView.findViewById(R.id.scrollView2);
        RVRefreshExpense = rootView.findViewById(R.id.swipeRefreshLayout);
        TextView budgetTV = rootView.findViewById(R.id.TVAmountBudget);
        TextView expenseTV = rootView.findViewById(R.id.TVAmountExpenses);
        TextView differenceTV = rootView.findViewById(R.id.TVAmountBalance);
        TextView monthTV = rootView.findViewById(R.id.TVMonth);
        monthTV.setText(formattedDate);
        //final double[] budget = {0};
        setUpRVExpense();
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);

        fbe.getBudget(0, new Firebase_Expenses.BudgetCallback() {
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
                fbe.calculateTotalExpense(new Firebase_Expenses.TotalExpenseCallback() {
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
                        try {
                            int progress = (int) ((newExpense / newBudget) * 100);
                            progressBar.setProgress(progress);
                        } catch (NumberFormatException e) {
                            // Handle the case where parsing fails
                            e.printStackTrace();
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


        RVRefreshExpense.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVExpense();
                fbe.getBudget(0, new Firebase_Expenses.BudgetCallback() {
                    @Override
                    public void onBudgetRetrieved(double budget) {
                        // Assign the retrieved budget to the array
                        //budget = retrievedBudget;

                        double newBudget;

                        if (budget % 1 == 0) {
                            // Convert to int if the decimal part is zero
                            newBudget = (int) budget;
                            budgetTV.setText("RM" + Integer.toString((int)newBudget));
                        } else {
                            newBudget = budget;
                            budgetTV.setText("RM" + Double.toString(newBudget));
                        }
                        fbe.calculateTotalExpense(new Firebase_Expenses.TotalExpenseCallback() {
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
                                try {
                                    int progress = (int) ((newExpense / newBudget) * 100);
                                    progressBar.setProgress(progress);
                                } catch (NumberFormatException e) {
                                    // Handle the case where parsing fails
                                    e.printStackTrace();
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

                RVRefreshExpense.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.CLRecyclerView, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assuming your ConstraintLayout has the ID 'book'
        Button addExpenseButton = view.findViewById(R.id.addExpenseButton);
        addExpenseButton.setOnClickListener(v -> {
            // Replace with the fragment you want to navigate to
            replaceFragment(new CalculatorFragment());

            // Make the button invisible
            //addExpenseButton.setVisibility(View.INVISIBLE);
            // If you want to use View.GONE instead, use the following line:
            // addExpenseButton.setVisibility(View.GONE);
        });
    }

    private void retrieveExpenses() {
        // Clear existing list to avoid duplicates
        expenseList.clear();

        // Query Firestore for expenses
        monthExpensesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Convert Firestore document to Expense object
                    Expense expense = document.toObject(Expense.class);
                    expenseList.add(expense);
                }

                // Update RecyclerView adapter
                Collections.sort(expenseList, new Comparator<Expense>() {
                    public int compare(Expense expense1, Expense expense2) {
                        // Compare the dates in reverse order (latest first)
                        return expense2.getDate().compareTo(expense1.getDate());
                    }
                });
                adapter.notifyDataSetChanged();
            } else {
                // Handle errors
                Log.e("BookFragment", "Error getting expenses", task.getException());
            }
        });
    }

    public void setUpRVExpense(){
        retrieveExpenses();
        adapter = new ExpenseAdapter(expenseList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }


}