package com.example.moneywise.expenses;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneywise.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetBudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetBudgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText etDb, etMeal, etDaily, etTransport, etCommunicate, etRecreation, etMedical, etOthers;

    // Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();
    DocumentReference userDocRef = db.collection("USER_DETAILS").document(userID);
    CollectionReference expensesCollection = userDocRef.collection("EXPENSE");
    DocumentReference budgetMonthDocRef = expensesCollection.document("EXPENSE_BUDGET_MONTH");
    Timestamp timestamp = Timestamp.now();
    Date currentDate = new Date(timestamp.getSeconds() * 1000); // Convert seconds to milliseconds
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
    String formattedDate = dateFormat.format(currentDate);
    CollectionReference monthBudgetCollection = budgetMonthDocRef.collection(formattedDate);

    private boolean toastShown = false;

    public SetBudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetBudgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetBudgetFragment newInstance(String param1, String param2) {
        SetBudgetFragment fragment = new SetBudgetFragment();
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
        View view = inflater.inflate(R.layout.fragment_set_budget, container, false);

        etDb = view.findViewById(R.id.ETBudgetDB);
        etMeal = view.findViewById(R.id.ETBudgetMeal);
        etDaily = view.findViewById(R.id.ETBudgetDaily);
        etTransport = view.findViewById(R.id.ETBudgetTransport);
        etCommunicate = view.findViewById(R.id.ETBudgetCommunicate);
        etRecreation = view.findViewById(R.id.ETBudgetRecreation);
        etMedical = view.findViewById(R.id.ETBudgetMedical);
        etOthers = view.findViewById(R.id.ETBudgetOthers);

        assignId(etDb, R.id.ETBudgetDB, 0);
        assignId(etMeal, R.id.ETBudgetMeal, 1);
        assignId(etDaily, R.id.ETBudgetDaily, 2);
        assignId(etTransport, R.id.ETBudgetTransport, 3);
        assignId(etCommunicate, R.id.ETBudgetCommunicate, 4);
        assignId(etRecreation, R.id.ETBudgetRecreation, 5);
        assignId(etMedical, R.id.ETBudgetMedical, 6);
        assignId(etOthers, R.id.ETBudgetOthers, 7);

        // Retrieve existing budget values from Firestore
        monthBudgetCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve budget values for each category
                    Long categoryId = document.getLong("category_id");
                    Object value = document.get("budget_amount");

                    if (categoryId != null && value != null) {
                        // Set the values in the corresponding EditText views
                        if (categoryId == 0) {
                            setEditTextValue(etDb, value);
                        } else if (categoryId == 1) {
                            setEditTextValue(etMeal, value);
                        } else if (categoryId == 2) {
                            setEditTextValue(etDaily, value);
                        } else if (categoryId == 3) {
                            setEditTextValue(etTransport, value);
                        } else if (categoryId == 4) {
                            setEditTextValue(etCommunicate, value);
                        } else if (categoryId == 5) {
                            setEditTextValue(etRecreation, value);
                        } else if (categoryId == 6) {
                            setEditTextValue(etMedical, value);
                        } else if (categoryId == 7) {
                            setEditTextValue(etOthers, value);
                        }
                    }
                }
            } else {
                // Handle failures
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });


        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.setBudgetCL, fragment);
        fragmentTransaction.commit();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assuming your ConstraintLayout has the ID 'book'
        view.findViewById(R.id.buttonDone).setOnClickListener(v -> {
            // Replace with the fragment you want to navigate to
            View someView = view.findViewById(R.id.setBudgetCL);
            someView.setVisibility(View.INVISIBLE);
            saveBudgetToFirestore(etDb);
            saveBudgetToFirestore(etMeal);
            saveBudgetToFirestore(etDaily);
            saveBudgetToFirestore(etTransport);
            saveBudgetToFirestore(etCommunicate);
            saveBudgetToFirestore(etRecreation);
            saveBudgetToFirestore(etMedical);
            saveBudgetToFirestore(etOthers);
            replaceFragment(new BudgetFragment());

        });


    }

    void assignId(EditText et, int id, int categoryId) {
        et.setId(id);
        et.setTag(categoryId);
    }

    // Callback for saving budget to Firestore
    private void saveBudgetToFirestore(EditText et) {

        // Get the selected category ID
        int categoryId = (int) et.getTag();

        // Get the entered value and description
        String value = et.getText().toString();
        // Check if the entered value is not empty
        if (TextUtils.isEmpty(value)) {
            // If it's empty, set a default or handle the case as needed
            value = "not set"; // You can replace this with your default value
        }


        // Create a new expense map
        Map<String, Object> budget = new HashMap<>();
        budget.put("category_id", categoryId);
        budget.put("budget_amount", value);

        // Check if the document already exists based on some condition
        Query query = monthBudgetCollection.whereEqualTo("category_id", categoryId);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Document already exists, update it
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        monthBudgetCollection.document(documentId).update(budget) .addOnSuccessListener(aVoid -> {
                                    if (!toastShown) {
                                        Toast.makeText(requireContext(), "Budget updated successfully", Toast.LENGTH_SHORT).show();
                                        toastShown = true;
                                    }
                                    // Clear the UI or perform any other necessary actions
                                })
                                .addOnFailureListener(e -> {
                                    if (!toastShown) {
                                        Toast.makeText(requireContext(), "Failed to update budget", Toast.LENGTH_SHORT).show();
                                        toastShown = true;
                                    }
                                    // Handle the error
                                });
                    } else {
                        // Document does not exist, add a new one
                        monthBudgetCollection.add(budget).addOnSuccessListener(documentReference -> {
                                    if (!toastShown) {
                                        Toast.makeText(requireContext(), "Budget saved successfully", Toast.LENGTH_SHORT).show();
                                        toastShown = true;
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to save budget", Toast.LENGTH_SHORT).show();
                                    // Handle the error
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to check for existing budget", Toast.LENGTH_SHORT).show();
                    // Handle the error
                });
    }

    // Set the value in the corresponding EditText based on the category
    private void setEditTextValue(EditText editText, Object value) {
        if (value != null && !value.toString().equals("not set")) {
            editText.setText(value.toString());
        } else {
            editText.setHint("not set");
        }
    }

}