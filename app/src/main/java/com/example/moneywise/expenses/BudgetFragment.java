package com.example.moneywise.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference categoriesCollection = db.collection("EXPENSE_CATEGORY");
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
    Firebase_Expenses fbe = new Firebase_Expenses();
    private CircularProgressBar pb0;
    private TextView tvPercent0, tvUsed0, tvBudget0;
    private TextView tvBudget1, tvUsed1, tvLeft1;
    private ProgressBar pb1;
    private TextView tvBudget2, tvUsed2, tvLeft2;
    private ProgressBar pb2;
    private TextView tvBudget3, tvUsed3, tvLeft3;
    private ProgressBar pb3;
    private TextView tvBudget4, tvUsed4, tvLeft4;
    private ProgressBar pb4;
    private TextView tvBudget5, tvUsed5, tvLeft5;
    private ProgressBar pb5;
    private TextView tvBudget6, tvUsed6, tvLeft6;
    private ProgressBar pb6;
    private TextView tvBudget7, tvUsed7, tvLeft7;
    private ProgressBar pb7;


    TextView budgetTV, mealBudgetTV, dailyBudgetTV, transportBudgetTV, communicationBudgetTV, recreationBudgetTV, medicalBudgetTV, othersBudgetTV;

    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BudgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        pb0 = rootView.findViewById(R.id.PBBudget);
        tvPercent0 = rootView.findViewById(R.id.TVBudgetPercent);
        tvUsed0 = rootView.findViewById(R.id.ExpenseTV3);
        tvBudget0 = rootView.findViewById(R.id.budgetTv);
        tvBudget1 = rootView.findViewById(R.id.tvBudgetMeal);
        tvUsed1 = rootView.findViewById(R.id.tvUsedMeal);
        tvLeft1 = rootView.findViewById(R.id.tvLeftMeal);
        pb1 = rootView.findViewById(R.id.progressBarMeal);

        tvBudget2 = rootView.findViewById(R.id.tvBudgetDaily);
        tvUsed2 = rootView.findViewById(R.id.tvUsedDaily);
        tvLeft2 = rootView.findViewById(R.id.tvLeftDaily);
        pb2 = rootView.findViewById(R.id.progressBarDaily);

        tvBudget3 = rootView.findViewById(R.id.tvBudgetTransport);
        tvUsed3 = rootView.findViewById(R.id.tvUsedTransport);
        tvLeft3 = rootView.findViewById(R.id.tvLeftTransport);
        pb3 = rootView.findViewById(R.id.progressBarTransport);

        tvBudget4 = rootView.findViewById(R.id.tvBudgetCommunicate);
        tvUsed4 = rootView.findViewById(R.id.tvUsedCommunicate);
        tvLeft4 = rootView.findViewById(R.id.tvLeftCommunicate);
        pb4 = rootView.findViewById(R.id.progressBarCommunicate);

        tvBudget5 = rootView.findViewById(R.id.tvBudgetRecreation);
        tvUsed5 = rootView.findViewById(R.id.tvUsedRecreation);
        tvLeft5 = rootView.findViewById(R.id.tvLeftRecreation);
        pb5 = rootView.findViewById(R.id.progressBarRecreation);

        tvBudget6 = rootView.findViewById(R.id.tvBudgetMedical);
        tvUsed6 = rootView.findViewById(R.id.tvUsedMedical);
        tvLeft6 = rootView.findViewById(R.id.tvLeftMedical);
        pb6 = rootView.findViewById(R.id.progressBarMedical);

        tvBudget7 = rootView.findViewById(R.id.tvBudgetOthers);
        tvUsed7 = rootView.findViewById(R.id.tvUsedOthers);
        tvLeft7 = rootView.findViewById(R.id.tvLeftOthers);
        pb7 = rootView.findViewById(R.id.progressBarOthers);

        setTV();
        setCategoryTV();

        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        ConstraintLayout budgetCL = rootView.findViewById(R.id.budgetCL);
        // Set refresh listener for SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setTV();
                setCategoryTV();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    // Method to replace the current fragment with another fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.budgetCL, fragment);
        fragmentTransaction.commit();
    }



    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set click listener for a button
        view.findViewById(R.id.buttonSetBudget).setOnClickListener(v -> {
            // Replace with the fragment you want to navigate to
            replaceFragment(new SetBudgetFragment());
        });

    }

    // Method to set overall budget information
    public void setTV(){
        fbe.getBudget(0, new Firebase_Expenses.BudgetCallback() {
            @Override
            public void onBudgetRetrieved(double budget) {
                // Assign the retrieved budget to the array
                //budget = retrievedBudget;

                double newBudget;

                if (budget % 1 == 0) {
                    // Convert to int if the decimal part is zero
                    newBudget = (int) budget;
                    tvBudget0.setText("RM" + Integer.toString((int)newBudget));
                    if (budget==0){
                        tvBudget0.setText("Not Set");
                    }
                } else {
                    newBudget = budget;
                    tvBudget0.setText("RM" + Double.toString(newBudget));
                }
                fbe.calculateTotalExpense(new Firebase_Expenses.TotalExpenseCallback() {
                    double newExpense;
                    @Override
                    public void onTotalExpenseCalculated(double totalExpense) {
                        if (totalExpense % 1 == 0) {
                            // Convert to int if the decimal part is zero
                            newExpense = (int) totalExpense;
                            tvUsed0.setText("RM" + Integer.toString((int)newExpense));
                        } else {
                            newExpense = totalExpense;
                            tvUsed0.setText("RM" + Double.toString(newExpense));
                        }

                        try {
                            float progress = (float) (newExpense / newBudget);
                            pb0.setProgress(progress);
                            tvPercent0.setText(String.format("%.2f%%", progress * 100));
                            if (newBudget == 0){
                                pb0.setProgress(0);
                                tvPercent0.setText("Not Set");
                            }
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

    }

    // Method to set budget information for specific expense categories
    public void setCategoryTV (){
        for (int i=1; i<=7; i++){
            int finalI = i;
            fbe.getBudget(i, new Firebase_Expenses.BudgetCallback() {
                @Override
                public void onBudgetRetrieved(double budgetC) {
                    // Assign the retrieved budget to the array
                    //budget = retrievedBudget;
                    double newBudgetC;

                    if (budgetC % 1 == 0) {
                        // Convert to int if the decimal part is zero
                        newBudgetC = (int) budgetC;
                        if (finalI ==1){
                            setBudgetText(tvBudget1, Integer.toString((int) newBudgetC));
                        } else if (finalI==2) {
                            setBudgetText(tvBudget2, Integer.toString((int) newBudgetC));
                        } else if (finalI==3) {
                            setBudgetText(tvBudget3, Integer.toString((int) newBudgetC));
                        } else if (finalI==4) {
                            setBudgetText(tvBudget4, Integer.toString((int) newBudgetC));
                        } else if (finalI==5) {
                            setBudgetText(tvBudget5, Integer.toString((int) newBudgetC));
                        } else if (finalI==6) {
                            setBudgetText(tvBudget6, Integer.toString((int) newBudgetC));
                        } else {
                            setBudgetText(tvBudget7, Integer.toString((int) newBudgetC));
                        }

                    } else {
                        newBudgetC = budgetC;
                        if (finalI ==1){
                            setBudgetText(tvBudget1, Double.toString(newBudgetC));
                        } else if (finalI==2) {
                            setBudgetText(tvBudget2, Double.toString(newBudgetC));
                        } else if (finalI==3) {
                            setBudgetText(tvBudget3, Double.toString(newBudgetC));
                        } else if (finalI==4) {
                            setBudgetText(tvBudget4, Double.toString(newBudgetC));
                        } else if (finalI==5) {
                            setBudgetText(tvBudget5, Double.toString(newBudgetC));
                        } else if (finalI==6) {
                            setBudgetText(tvBudget6, Double.toString(newBudgetC));
                        } else {
                            setBudgetText(tvBudget7, Double.toString(newBudgetC));
                        }
                    }
                    fbe.calculateCategoryExpense(finalI, new Firebase_Expenses.CategoryExpenseCallback(){
                        @Override
                        public void onCategoryExpenseCalculated(double categoryExpense) {
                            if (newBudgetC>0){
                                double diff = newBudgetC-categoryExpense;
                                double progress = (categoryExpense/newBudgetC)*100;
                                if (finalI==1){
                                    setVisibile(tvUsed1, tvLeft1, pb1);
                                    setExpenseText(tvUsed1, categoryExpense);
                                    setExpenseText(tvLeft1, diff);
                                    pb1.setProgress((int) progress);
                                } else if (finalI==2){
                                    setVisibile(tvUsed2, tvLeft2, pb2);
                                    setExpenseText(tvUsed2, categoryExpense);
                                    setExpenseText(tvLeft2, diff);
                                    pb2.setProgress((int) progress);
                                } else if (finalI==3){
                                    setVisibile(tvUsed3, tvLeft3, pb3);
                                    setExpenseText(tvUsed3, categoryExpense);
                                    setExpenseText(tvLeft3, diff);
                                    pb3.setProgress((int) progress);
                                } else if (finalI==4){
                                    setVisibile(tvUsed4, tvLeft4, pb4);
                                    setExpenseText(tvUsed4, categoryExpense);
                                    setExpenseText(tvLeft4, diff);
                                    pb4.setProgress((int) progress);
                                } else if (finalI==5){
                                    setVisibile(tvUsed5, tvLeft5, pb5);
                                    setExpenseText(tvUsed5, categoryExpense);
                                    setExpenseText(tvLeft5, diff);
                                    pb5.setProgress((int) progress);
                                } else if (finalI==6){
                                    setVisibile(tvUsed6, tvLeft6, pb6);
                                    setExpenseText(tvUsed6, categoryExpense);
                                    setExpenseText(tvLeft6, diff);
                                    pb6.setProgress((int) progress);
                                } else if (finalI==7){
                                    setVisibile(tvUsed7, tvLeft7, pb7);
                                    setExpenseText(tvUsed7, categoryExpense);
                                    setExpenseText(tvLeft7, diff);
                                    pb7.setProgress((int) progress);
                                }

                            }else {
                                if (finalI==1){
                                    setVisibility(tvUsed1, tvLeft1, pb1);
                                } else if (finalI==2){
                                    setVisibility(tvUsed2, tvLeft2, pb2);
                                } else if (finalI==3){
                                    setVisibility(tvUsed3, tvLeft3, pb3);
                                } else if (finalI==4){
                                    setVisibility(tvUsed4, tvLeft4, pb4);
                                } else if (finalI==5){
                                    setVisibility(tvUsed5, tvLeft5, pb5);
                                } else if (finalI==6){
                                    setVisibility(tvUsed6, tvLeft6, pb6);
                                } else if (finalI==7){
                                    setVisibility(tvUsed7, tvLeft7, pb7);
                                }
                            }


                        }

                        @Override
                        public void onError(Exception exception) {
                            exception.printStackTrace();
                        }
                    });

                }

            });
        }
    }

    // Method to set budget text based on conditions
    public void setBudgetText (TextView tv, String budget){
        if (budget.equals("0")){
            tv.setText("not set");
        } else {
            tv.setText("RM"+budget);
        }
    }

    // Method to set expense text based on conditions
    public void setExpenseText (TextView tv, double expense){
        if (expense%1==0){
            tv.setText("RM"+Integer.toString((int)expense));
        }
        else{
            tv.setText("RM"+Double.toString(expense));
        }
    }

    // Method to set visibility of TextViews and ProgressBar
    public void setVisibility (TextView tv1, TextView tv2, ProgressBar pb){
        tv1.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.INVISIBLE);
    }

    // Method to set visibility of TextViews and ProgressBar
    public void setVisibile (TextView tv1, TextView tv2, ProgressBar pb){
        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

}