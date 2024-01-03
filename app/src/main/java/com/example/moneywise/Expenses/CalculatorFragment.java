package com.example.moneywise.Expenses;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneywise.R;
import com.example.moneywise.databinding.FragmentCalculatorBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalculatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalculatorFragment extends Fragment implements View.OnClickListener{

    private FragmentCalculatorBinding binding;
    //private View view;
    TextView resultTv, descTv;
    //MaterialButton buttonPlus, buttonMinus, buttonMultiple, buttonDivide;
    //MaterialButton buttonPoint, buttonCancel;
    //MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
   // Button buttonMeal, buttonDaily, buttonTransport, buttonCommunicate;
   // Button buttonRecreation, buttonMedical, buttonOthers;

    Button currentlySelectedButton;
    ImageButton buttonConfirmAll, buttonCancelAll;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();

    public CalculatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalculatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalculatorFragment newInstance(String param1, String param2) {
        CalculatorFragment fragment = new CalculatorFragment();
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
        binding = FragmentCalculatorBinding.inflate(inflater, container, false);
        //view = inflater.inflate(R.layout.fragment_calculator, container, false);

        return binding.getRoot();
    }




    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.calcCL, fragment);
        fragmentTransaction.commit();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assuming your ConstraintLayout has the ID 'book'
        view.findViewById(R.id.btnConfirmAll).setOnClickListener(v -> {
            // Replace with the fragment you want to navigate to
            saveExpenseToFirestore();
            View someView = view.findViewById(R.id.calcCL);
            someView.setVisibility(View.INVISIBLE);
            replaceFragment(new BookFragment());
        });

        view.findViewById(R.id.btnCancelAll).setOnClickListener(v -> {
            // Replace with the fragment you want to navigate to
            View someView = view.findViewById(R.id.calcCL);
            someView.setVisibility(View.INVISIBLE);
            replaceFragment(new BookFragment());
        });

        // Initialize resultTv
        resultTv = binding.resultTV;
        descTv = binding.descET;


        // Assign IDs and set OnClickListener for buttons
        assignId(binding.buttonPoint, R.id.button_Point);
        assignId(binding.buttonCancel, R.id.button_Cancel);
        assignId(binding.button0, R.id.button_0);
        assignId(binding.button1, R.id.button_1);
        assignId(binding.button2, R.id.button_2);
        assignId(binding.button3, R.id.button_3);
        assignId(binding.button4, R.id.button_4);
        assignId(binding.button5, R.id.button_5);
        assignId(binding.button6, R.id.button_6);
        assignId(binding.button7, R.id.button_7);
        assignId(binding.button8, R.id.button_8);
        assignId(binding.button9, R.id.button_9);

        assignId(binding.buttonMeal, R.id.button_Meal, 1);
        assignId(binding.buttonDaily, R.id.button_Daily, 2);
        assignId(binding.buttonTransport, R.id.button_Transport, 3);
        assignId(binding.buttonCommunicate, R.id.button_Communicate, 4);
        assignId(binding.buttonRecreation, R.id.button_Recreation, 5);
        assignId(binding.buttonMedical, R.id.button_Medical, 6);
        assignId(binding.buttonOthers, R.id.button_Others, 7);

        binding.buttonMeal.setOnClickListener(v -> onButtonClick(binding.buttonMeal));
        binding.buttonDaily.setOnClickListener(v -> onButtonClick(binding.buttonDaily));
        binding.buttonTransport.setOnClickListener(v -> onButtonClick(binding.buttonTransport));
        binding.buttonCommunicate.setOnClickListener(v -> onButtonClick(binding.buttonCommunicate));
        binding.buttonRecreation.setOnClickListener(v -> onButtonClick(binding.buttonRecreation));
        binding.buttonMedical.setOnClickListener(v -> onButtonClick(binding.buttonMedical));
        binding.buttonOthers.setOnClickListener(v -> onButtonClick(binding.buttonOthers));

        //assignId(binding.btnConfirmAll, R.id.btnConfirmAll);
        //assignId(binding.btnCancelAll, R.id.btnCancelAll);

    }

    void assignId(MaterialButton btn, int id) {
        btn.setId(id);
        btn.setOnClickListener(this);
    }

    void assignId(Button btn, int id, int categoryId) {
        btn.setId(id);
        btn.setTag(categoryId);
        btn.setOnClickListener(this);
    }

    void assignId(ImageButton btn, int id) {
        btn.setId(id);
        btn.setOnClickListener(this);
    }

    public void onClick (View view){
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String data = resultTv.getText().toString();

        if (data.equals("0")&&buttonText.equals("")){
            data = "0";
        }
        else if (data.equals("0")&&!buttonText.equals(".")){
            data = buttonText;
        }
        else
            data = data+buttonText;

        if(buttonText.equals("")) {
            data = data.substring(0, data.length() - 1);
            if (data.equals(""))
                data = "0";
        }

        resultTv.setText(data);
    }

    private void onButtonClick(Button clickedButton) {
        if (currentlySelectedButton != null) {
            currentlySelectedButton.setBackgroundColor(Color.parseColor("#fffeec"));
        }

        clickedButton.setBackgroundColor(Color.parseColor("#fddd5c"));
        currentlySelectedButton = clickedButton;
    }

    private void saveExpenseToFirestore() {
        if (currentlySelectedButton == null) {
            // No category selected
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected category ID
        int categoryId = (int) currentlySelectedButton.getTag();

        // Get the entered value and description
        String value = resultTv.getText().toString();
        String description = descTv.getText().toString();

        // Get the current timestamp
        Timestamp timestamp = Timestamp.now();
        Date currentDate = new Date(timestamp.getSeconds() * 1000); // Convert seconds to milliseconds
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
        String formattedDate = dateFormat.format(currentDate);
        SimpleDateFormat dbDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        String formattedDbDate = dbDate.format(currentDate);

        // Create a new expense map
        Map<String, Object> expense = new HashMap<>();
        expense.put("category_id", categoryId);
        expense.put("expense_amount", value);
        expense.put("description", description);
        expense.put("date", formattedDbDate);

        // Access or create the user's document in "USER_DETAILS" collection
        DocumentReference userDocRef = db.collection("USER_DETAILS").document(userID);

        // Access or create the "expenses" subcollection for the current user
        CollectionReference expensesCollection = userDocRef.collection("EXPENSE");

        // Access or create the document for the current month and year
        DocumentReference monthDocRef = expensesCollection.document("EXPENSE_MONTH");

        // Access or create the "expense" subcollection for the current month
        CollectionReference monthExpensesCollection = monthDocRef.collection(formattedDate);

        // Add the expense to Firestore
        monthExpensesCollection.add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Expense saved successfully", Toast.LENGTH_SHORT).show();
                    // Clear the UI or perform any other necessary actions
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save expense", Toast.LENGTH_SHORT).show();
                    // Handle the error
                });
    }

}