package com.example.moneywise.Expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.moneywise.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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
    DocumentReference budgetMonthDocRef = expensesCollection.document("EXPENSE_BUDGET_MONTH");
    TextView tvE1, tvE2, tvE3, tvE4, tvE5, tvE6, tvE7, tvE8, tvE9, tvE10, tvE11, tvE12;
    TextView tvB1, tvB2, tvB3, tvB4, tvB5, tvB6, tvB7, tvB8, tvB9, tvB10, tvB11, tvB12;
    TextView tvL1, tvL2, tvL3, tvL4, tvL5, tvL6, tvL7, tvL8, tvL9, tvL10, tvL11, tvL12;
    TextView tvYear;
    View cl1, cl2, cl3, cl4, cl5, cl6, cl7, cl8, cl9, cl10, cl11, cl12;
    Firebase_Expenses fbe = new Firebase_Expenses();

    public TrendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendFragment newInstance(String param1, String param2) {
        TrendFragment fragment = new TrendFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_trend, container, false);

        cl1 = rootView.findViewById(R.id.cl1);
        cl2 = rootView.findViewById(R.id.cl2);
        cl3 = rootView.findViewById(R.id.cl3);
        cl4 = rootView.findViewById(R.id.cl4);
        cl5 = rootView.findViewById(R.id.cl5);
        cl6 = rootView.findViewById(R.id.cl6);
        cl7 = rootView.findViewById(R.id.cl7);
        cl8 = rootView.findViewById(R.id.cl8);
        cl9 = rootView.findViewById(R.id.cl9);
        cl10 = rootView.findViewById(R.id.cl10);
        cl11 = rootView.findViewById(R.id.cl11);
        cl12 = rootView.findViewById(R.id.cl12);

        tvE1 = rootView.findViewById(R.id.tvExpense1);
        tvE2 = rootView.findViewById(R.id.tvExpense2);
        tvE3 = rootView.findViewById(R.id.tvExpense3);
        tvE4 = rootView.findViewById(R.id.tvExpense4);
        tvE5 = rootView.findViewById(R.id.tvExpense5);
        tvE6 = rootView.findViewById(R.id.tvExpense6);
        tvE7 = rootView.findViewById(R.id.tvExpense7);
        tvE8 = rootView.findViewById(R.id.tvExpense8);
        tvE9 = rootView.findViewById(R.id.tvExpense9);
        tvE10 = rootView.findViewById(R.id.tvExpense10);
        tvE11 = rootView.findViewById(R.id.tvExpense11);
        tvE12 = rootView.findViewById(R.id.tvExpense12);

        tvB1 = rootView.findViewById(R.id.tvBudget1);
        tvB2 = rootView.findViewById(R.id.tvBudget2);
        tvB3 = rootView.findViewById(R.id.tvBudget3);
        tvB4 = rootView.findViewById(R.id.tvBudget4);
        tvB5 = rootView.findViewById(R.id.tvBudget5);
        tvB6 = rootView.findViewById(R.id.tvBudget6);
        tvB7 = rootView.findViewById(R.id.tvBudget7);
        tvB8 = rootView.findViewById(R.id.tvBudget8);
        tvB9 = rootView.findViewById(R.id.tvBudget9);
        tvB10 = rootView.findViewById(R.id.tvBudget10);
        tvB11 = rootView.findViewById(R.id.tvBudget11);
        tvB12 = rootView.findViewById(R.id.tvBudget12);

        tvL1 = rootView.findViewById(R.id.tvBalance1);
        tvL2 = rootView.findViewById(R.id.tvBalance2);
        tvL3 = rootView.findViewById(R.id.tvBalance3);
        tvL4 = rootView.findViewById(R.id.tvBalance4);
        tvL5 = rootView.findViewById(R.id.tvBalance5);
        tvL6 = rootView.findViewById(R.id.tvBalance6);
        tvL7 = rootView.findViewById(R.id.tvBalance7);
        tvL8 = rootView.findViewById(R.id.tvBalance8);
        tvL9 = rootView.findViewById(R.id.tvBalance9);
        tvL10 = rootView.findViewById(R.id.tvBalance10);
        tvL11 = rootView.findViewById(R.id.tvBalance11);
        tvL12 = rootView.findViewById(R.id.tvBalance12);

        tvYear = rootView.findViewById(R.id.tvYear);
        // Get the current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        // Set the text of the TextView to the current year
        tvYear.setText(String.valueOf(currentYear));

        // Find the LineGraph view within the fragment's layout
        LineGraph lineGraph = rootView.findViewById(R.id.lineGraph);

        // Set data to the LineGraph
        ArrayList<Double> dataPoints = new ArrayList<>();

        // Get the current date
        Calendar currentDate = Calendar.getInstance();

        // Create a SimpleDateFormat to format the month and year
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        //currentMonth = Calendar.JULY;
        setVisibility(currentMonth);

        for (int month = Calendar.JANUARY; month <= currentMonth; month++) {
            // Set the current month in the Calendar
            currentDate.set(Calendar.MONTH, month);

            // Format the date to get the month and year string
            String formattedDate = dateFormat.format(currentDate.getTime());

            // Create the collection reference for the current month
            CollectionReference monthExpensesCollection = monthDocRef.collection(formattedDate);
            CollectionReference monthBudgetCollection = budgetMonthDocRef.collection(formattedDate);

            // Example: Fetching documents from the current month
            int finalMonth = month;

            // Fetching month-wise budget data from Firestore
            monthBudgetCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    // If budget documents exist, fetch month budget data
                    if (documents != null && !documents.isEmpty()) {
                        fbe.getMonthBudget(monthBudgetCollection, 0, new Firebase_Expenses.MonthBudgetCallback() {

                            @Override
                            public void onMonthBudgetRetrieved(double monthBudget) {
                                // Handle case when month budget is not set
                                if (monthBudget==0) {
                                    if (finalMonth == Calendar.JANUARY){
                                        tvB1.setText("Not Set");
                                    } else if (finalMonth == Calendar.FEBRUARY) {
                                        tvB2.setText("Not Set");
                                    } else if (finalMonth == Calendar.MARCH) {
                                        tvB3.setText("Not Set");
                                    } else if (finalMonth == Calendar.APRIL) {
                                        tvB4.setText("Not Set");
                                    } else if (finalMonth == Calendar.MAY) {
                                        tvB5.setText("Not Set");
                                    } else if (finalMonth == Calendar.JUNE) {
                                        tvB6.setText("Not Set");
                                    } else if (finalMonth == Calendar.JULY) {
                                        tvB7.setText("Not Set");
                                    } else if (finalMonth == Calendar.AUGUST) {
                                        tvB8.setText("Not Set");
                                    } else if (finalMonth == Calendar.SEPTEMBER) {
                                        tvB9.setText("Not Set");
                                    } else if (finalMonth == Calendar.OCTOBER) {
                                        tvB10.setText("Not Set");
                                    } else if (finalMonth == Calendar.NOVEMBER) {
                                        tvB11.setText("Not Set");
                                    } else {
                                        tvB12.setText("Not Set");
                                    }
                                } else {
                                    if (finalMonth == Calendar.JANUARY){
                                        setText(tvB1, monthBudget);
                                    } else if (finalMonth == Calendar.FEBRUARY) {
                                        setText(tvB2, monthBudget);
                                    } else if (finalMonth == Calendar.MARCH) {
                                        setText(tvB3, monthBudget);
                                    } else if (finalMonth == Calendar.APRIL) {
                                        setText(tvB4, monthBudget);
                                    } else if (finalMonth == Calendar.MAY) {
                                        setText(tvB5, monthBudget);
                                    } else if (finalMonth == Calendar.JUNE) {
                                        setText(tvB6, monthBudget);
                                    } else if (finalMonth == Calendar.JULY) {
                                        setText(tvB7, monthBudget);
                                    } else if (finalMonth == Calendar.AUGUST) {
                                        setText(tvB8, monthBudget);
                                    } else if (finalMonth == Calendar.SEPTEMBER) {
                                        setText(tvB9, monthBudget);
                                    } else if (finalMonth == Calendar.OCTOBER) {
                                        setText(tvB10, monthBudget);
                                    } else if (finalMonth == Calendar.NOVEMBER) {
                                        setText(tvB11, monthBudget);
                                    } else {
                                        setText(tvB12, monthBudget);
                                    }
                                }
                                // Fetch month-wise expense data
                                monthExpensesCollection.get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        List<DocumentSnapshot> documents2 = task2.getResult().getDocuments();
                                        if (documents2 != null && !documents2.isEmpty()) {
                                            // Calculate and display month expense data
                                            fbe.calculateMonthExpense(monthExpensesCollection, new Firebase_Expenses.MonthExpenseCallback() {
                                                @Override
                                                public void onMonthExpenseCalculated(double totalExpense) {
                                                    if (finalMonth == Calendar.JANUARY){
                                                        setText(tvE1, totalExpense);
                                                    } else if (finalMonth == Calendar.FEBRUARY) {
                                                        setText(tvE2, totalExpense);
                                                    } else if (finalMonth == Calendar.MARCH) {
                                                        setText(tvE3, totalExpense);
                                                    } else if (finalMonth == Calendar.APRIL) {
                                                        setText(tvE4, totalExpense);
                                                    } else if (finalMonth == Calendar.MAY) {
                                                        setText(tvE5, totalExpense);
                                                    } else if (finalMonth == Calendar.JUNE) {
                                                        setText(tvE6, totalExpense);
                                                    } else if (finalMonth == Calendar.JULY) {
                                                        setText(tvE7, totalExpense);
                                                    } else if (finalMonth == Calendar.AUGUST) {
                                                        setText(tvE8, totalExpense);
                                                    } else if (finalMonth == Calendar.SEPTEMBER) {
                                                        setText(tvE9, totalExpense);
                                                    } else if (finalMonth == Calendar.OCTOBER) {
                                                        setText(tvE10, totalExpense);
                                                    } else if (finalMonth == Calendar.NOVEMBER) {
                                                        setText(tvE11, totalExpense);
                                                    } else {
                                                        setText(tvE12, totalExpense);
                                                    }
                                                    // Update line graph data points and UI
                                                    dataPoints.add(totalExpense);
                                                    lineGraph.setData(dataPoints);
                                                    if (monthBudget>0 || totalExpense>0){
                                                        // Calculate and display the budget difference
                                                        double difference = monthBudget - totalExpense;
                                                        if (finalMonth == Calendar.JANUARY){
                                                            setText(tvL1, difference);
                                                        } else if (finalMonth == Calendar.FEBRUARY) {
                                                            setText(tvL2, difference);
                                                        } else if (finalMonth == Calendar.MARCH) {
                                                            setText(tvL3, difference);
                                                        } else if (finalMonth == Calendar.APRIL) {
                                                            setText(tvL4, difference);
                                                        } else if (finalMonth == Calendar.MAY) {
                                                            setText(tvL5, difference);
                                                        } else if (finalMonth == Calendar.JUNE) {
                                                            setText(tvL6, difference);
                                                        } else if (finalMonth == Calendar.JULY) {
                                                            setText(tvL7, difference);
                                                        } else if (finalMonth == Calendar.AUGUST) {
                                                            setText(tvL8, difference);
                                                        } else if (finalMonth == Calendar.SEPTEMBER) {
                                                            setText(tvL9, difference);
                                                        } else if (finalMonth == Calendar.OCTOBER) {
                                                            setText(tvL10, difference);
                                                        } else if (finalMonth == Calendar.NOVEMBER) {
                                                            setText(tvL11, difference);
                                                        } else {
                                                            setText(tvL12, difference);
                                                        }

                                                    } else {
                                                        if (finalMonth == Calendar.JANUARY){
                                                            setText(tvL1, 0);
                                                        } else if (finalMonth == Calendar.FEBRUARY) {
                                                            setText(tvL2, 0);
                                                        } else if (finalMonth == Calendar.MARCH) {
                                                            setText(tvL3, 0);
                                                        } else if (finalMonth == Calendar.APRIL) {
                                                            setText(tvL4, 0);
                                                        } else if (finalMonth == Calendar.MAY) {
                                                            setText(tvL5, 0);
                                                        } else if (finalMonth == Calendar.JUNE) {
                                                            setText(tvL6, 0);
                                                        } else if (finalMonth == Calendar.JULY) {
                                                            setText(tvL7, 0);
                                                        } else if (finalMonth == Calendar.AUGUST) {
                                                            setText(tvL8, 0);
                                                        } else if (finalMonth == Calendar.SEPTEMBER) {
                                                            setText(tvL9, 0);
                                                        } else if (finalMonth == Calendar.OCTOBER) {
                                                            setText(tvL10, 0);
                                                        } else if (finalMonth == Calendar.NOVEMBER) {
                                                            setText(tvL11, 0);
                                                        } else {
                                                            setText(tvL12, 0);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onError(Exception exception) {
                                                    // Handle the error, e.g., log it or display a message
                                                    exception.printStackTrace();
                                                }
                                            });

                                        } else {
                                            if (finalMonth == Calendar.JANUARY){
                                                setText(tvE1, 0);
                                            } else if (finalMonth == Calendar.FEBRUARY) {
                                                setText(tvE2, 0);
                                            } else if (finalMonth == Calendar.MARCH) {
                                                setText(tvE3, 0);
                                            } else if (finalMonth == Calendar.APRIL) {
                                                setText(tvE4, 0);
                                            } else if (finalMonth == Calendar.MAY) {
                                                setText(tvE5, 0);
                                            } else if (finalMonth == Calendar.JUNE) {
                                                setText(tvE6, 0);
                                            } else if (finalMonth == Calendar.JULY) {
                                                setText(tvE7, 0);
                                            } else if (finalMonth == Calendar.AUGUST) {
                                                setText(tvE8, 0);
                                            } else if (finalMonth == Calendar.SEPTEMBER) {
                                                setText(tvE9, 0);
                                            } else if (finalMonth == Calendar.OCTOBER) {
                                                setText(tvE10, 0);
                                            } else if (finalMonth == Calendar.NOVEMBER) {
                                                setText(tvE11, 0);
                                            } else {
                                                setText(tvE12, 0);
                                            }
                                            dataPoints.add((double) 0);
                                            lineGraph.setData(dataPoints);
                                        }

                                    } else {
                                        // Handle the error
                                        Exception exception = task2.getException();
                                        if (exception != null) {
                                            exception.printStackTrace();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception exception) {
                                exception.printStackTrace();
                            }
                        });
                    } else {
                        // Handle case when no month budget documents exist
                        if (finalMonth == Calendar.JANUARY){
                            setText(tvB1, 0);
                        } else if (finalMonth == Calendar.FEBRUARY) {
                            setText(tvB2, 0);
                        } else if (finalMonth == Calendar.MARCH) {
                            setText(tvB3, 0);
                        } else if (finalMonth == Calendar.APRIL) {
                            setText(tvB4, 0);
                        } else if (finalMonth == Calendar.MAY) {
                            setText(tvB5, 0);
                        } else if (finalMonth == Calendar.JUNE) {
                            setText(tvB6, 0);
                        } else if (finalMonth == Calendar.JULY) {
                            setText(tvB7, 0);
                        } else if (finalMonth == Calendar.AUGUST) {
                            setText(tvB8, 0);
                        } else if (finalMonth == Calendar.SEPTEMBER) {
                            setText(tvB9, 0);
                        } else if (finalMonth == Calendar.OCTOBER) {
                            setText(tvB10, 0);
                        } else if (finalMonth == Calendar.NOVEMBER) {
                            setText(tvB11, 0);
                        } else {
                            setText(tvB12, 0);
                        }
                        monthExpensesCollection.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                List<DocumentSnapshot> documents2 = task.getResult().getDocuments();
                                if (documents2 != null && !documents2.isEmpty()) {
                                    fbe.calculateMonthExpense(monthExpensesCollection, new Firebase_Expenses.MonthExpenseCallback() {
                                        @Override
                                        public void onMonthExpenseCalculated(double totalExpense) {
                                            if (finalMonth == Calendar.JANUARY){
                                                setText(tvE1, totalExpense);
                                            } else if (finalMonth == Calendar.FEBRUARY) {
                                                setText(tvE2, totalExpense);
                                            } else if (finalMonth == Calendar.MARCH) {
                                                setText(tvE3, totalExpense);
                                            } else if (finalMonth == Calendar.APRIL) {
                                                setText(tvE4, totalExpense);
                                            } else if (finalMonth == Calendar.MAY) {
                                                setText(tvE5, totalExpense);
                                            } else if (finalMonth == Calendar.JUNE) {
                                                setText(tvE6, totalExpense);
                                            } else if (finalMonth == Calendar.JULY) {
                                                setText(tvE7, totalExpense);
                                            } else if (finalMonth == Calendar.AUGUST) {
                                                setText(tvE8, totalExpense);
                                            } else if (finalMonth == Calendar.SEPTEMBER) {
                                                setText(tvE9, totalExpense);
                                            } else if (finalMonth == Calendar.OCTOBER) {
                                                setText(tvE10, totalExpense);
                                            } else if (finalMonth == Calendar.NOVEMBER) {
                                                setText(tvE11, totalExpense);
                                            } else {
                                                setText(tvE12, totalExpense);
                                            }
                                            dataPoints.add(totalExpense);
                                            lineGraph.setData(dataPoints);
                                            if (totalExpense>0){
                                                double difference = 0 - totalExpense;
                                                if (finalMonth == Calendar.JANUARY){
                                                    setText(tvL1, difference);
                                                } else if (finalMonth == Calendar.FEBRUARY) {
                                                    setText(tvL2, difference);
                                                } else if (finalMonth == Calendar.MARCH) {
                                                    setText(tvL3, difference);
                                                } else if (finalMonth == Calendar.APRIL) {
                                                    setText(tvL4, difference);
                                                } else if (finalMonth == Calendar.MAY) {
                                                    setText(tvL5, difference);
                                                } else if (finalMonth == Calendar.JUNE) {
                                                    setText(tvL6, difference);
                                                } else if (finalMonth == Calendar.JULY) {
                                                    setText(tvL7, difference);
                                                } else if (finalMonth == Calendar.AUGUST) {
                                                    setText(tvL8, difference);
                                                } else if (finalMonth == Calendar.SEPTEMBER) {
                                                    setText(tvL9, difference);
                                                } else if (finalMonth == Calendar.OCTOBER) {
                                                    setText(tvL10, difference);
                                                } else if (finalMonth == Calendar.NOVEMBER) {
                                                    setText(tvL11, difference);
                                                } else {
                                                    setText(tvL12, difference);
                                                }
                                            } else {
                                                if (finalMonth == Calendar.JANUARY){
                                                    setText(tvL1, 0);
                                                } else if (finalMonth == Calendar.FEBRUARY) {
                                                    setText(tvL2, 0);
                                                } else if (finalMonth == Calendar.MARCH) {
                                                    setText(tvL3, 0);
                                                } else if (finalMonth == Calendar.APRIL) {
                                                    setText(tvL4, 0);
                                                } else if (finalMonth == Calendar.MAY) {
                                                    setText(tvL5, 0);
                                                } else if (finalMonth == Calendar.JUNE) {
                                                    setText(tvL6, 0);
                                                } else if (finalMonth == Calendar.JULY) {
                                                    setText(tvL7, 0);
                                                } else if (finalMonth == Calendar.AUGUST) {
                                                    setText(tvL8, 0);
                                                } else if (finalMonth == Calendar.SEPTEMBER) {
                                                    setText(tvL9, 0);
                                                } else if (finalMonth == Calendar.OCTOBER) {
                                                    setText(tvL10, 0);
                                                } else if (finalMonth == Calendar.NOVEMBER) {
                                                    setText(tvL11, 0);
                                                } else {
                                                    setText(tvL12, 0);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(Exception exception) {
                                            // Handle the error, e.g., log it or display a message
                                            exception.printStackTrace();
                                        }
                                    });

                                } else {
                                    if (finalMonth == Calendar.JANUARY){
                                        setText(tvE1, 0);
                                        setText(tvL1, 0);
                                    } else if (finalMonth == Calendar.FEBRUARY) {
                                        setText(tvE2, 0);
                                        setText(tvL2, 0);
                                    } else if (finalMonth == Calendar.MARCH) {
                                        setText(tvE3, 0);
                                        setText(tvL3, 0);
                                    } else if (finalMonth == Calendar.APRIL) {
                                        setText(tvE4, 0);
                                        setText(tvL4, 0);
                                    } else if (finalMonth == Calendar.MAY) {
                                        setText(tvE5, 0);
                                        setText(tvL5, 0);
                                    } else if (finalMonth == Calendar.JUNE) {
                                        setText(tvE6, 0);
                                        setText(tvL6, 0);
                                    } else if (finalMonth == Calendar.JULY) {
                                        setText(tvE7, 0);
                                        setText(tvL7, 0);
                                    } else if (finalMonth == Calendar.AUGUST) {
                                        setText(tvE8, 0);
                                        setText(tvL8, 0);
                                    } else if (finalMonth == Calendar.SEPTEMBER) {
                                        setText(tvE9, 0);
                                        setText(tvL9, 0);
                                    } else if (finalMonth == Calendar.OCTOBER) {
                                        setText(tvE10, 0);
                                        setText(tvL10, 0);
                                    } else if (finalMonth == Calendar.NOVEMBER) {
                                        setText(tvE11, 0);
                                        setText(tvL11, 0);
                                    } else {
                                        setText(tvE12, 0);
                                        setText(tvL12, 0);
                                    }
                                    dataPoints.add((double) 0);
                                    lineGraph.setData(dataPoints);
                                }

                            } else {
                                // Handle the error
                                Exception exception = task.getException();
                                if (exception != null) {
                                    exception.printStackTrace();
                                }
                            }
                        });
                    }


                } else {
                    // Handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });

        }


        return rootView;
    }

    // Update UI with formatted text
    public void setText (TextView tv, double value){
        if (value%1==0){
            tv.setText("RM"+Integer.toString((int)value));
        }
        else{
            tv.setText("RM"+Double.toString(value));
        }
    }

    //Handle visibility based on the selected month
    public void setVisibility (int m){
        if (m<Calendar.FEBRUARY)
            cl2.setVisibility(View.GONE);
        if (m<Calendar.MARCH)
            cl3.setVisibility(View.GONE);
        if (m<Calendar.APRIL)
            cl4.setVisibility(View.GONE);
        if (m<Calendar.MAY)
            cl5.setVisibility(View.GONE);
        if (m<Calendar.JUNE)
            cl6.setVisibility(View.GONE);
        if (m<Calendar.JULY)
            cl7.setVisibility(View.GONE);
        if (m<Calendar.AUGUST)
            cl8.setVisibility(View.GONE);
        if (m<Calendar.SEPTEMBER)
            cl9.setVisibility(View.GONE);
        if (m<Calendar.OCTOBER)
            cl10.setVisibility(View.GONE);
        if (m<Calendar.NOVEMBER)
            cl11.setVisibility(View.GONE);
        if (m<Calendar.DECEMBER)
            cl12.setVisibility(View.GONE);

    }

}