package com.example.moneywise.expenses;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class Firebase_Expenses {
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
    SimpleDateFormat dbDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    String formattedDbDate = dbDate.format(currentDate);
    DocumentReference monthDocRef = expensesCollection.document("EXPENSE_MONTH");
    CollectionReference monthExpensesCollection = monthDocRef.collection(formattedDate);

    // Callback interface for retrieving budget
    public interface BudgetCallback {
        void onBudgetRetrieved(double budget);
    }

    // Method to get budget for a category
    public void getBudget(int categoryID, BudgetCallback callback) {
        AtomicReference<Double> budget = new AtomicReference<>((double) 0);
        monthBudgetCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve budget values for each category
                    Long categoryId = document.getLong("category_id");
                    String value = document.getString("budget_amount");

                    if (categoryId != null && !value.equals("not set") && categoryId == categoryID) {
                        // Set the values in the corresponding EditText views
                        budget.set(Double.parseDouble(value));
                        break;
                    }
                }
                // Call the callback with the retrieved budget
                callback.onBudgetRetrieved(budget.get());
            } else {
                // Handle failures
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
                // Call the callback with a default value or an indication of failure
                callback.onBudgetRetrieved((double) 0);
            }
        });
    }

    // Callback interface for calculating total expense
    public interface TotalExpenseCallback {
        void onTotalExpenseCalculated(double totalExpense);
        void onError(Exception exception);
    }

    // Method to calculate total expense
    public void calculateTotalExpense(TotalExpenseCallback callback) {
        double[] totalExpense = {0}; // Using an array to make it effectively final

        monthExpensesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String expenseAmountStr = document.getString("expense_amount");
                    if (expenseAmountStr != null) {
                        double expenseAmount = Double.parseDouble(expenseAmountStr);
                        totalExpense[0] += expenseAmount;
                    }
                }
                // Now 'totalExpense[0]' contains the sum of all "expense_amount" values
                callback.onTotalExpenseCalculated(totalExpense[0]);
            } else {
                // Handle failures
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                    callback.onError(exception);
                }
            }
        });
    }

    // Callback interface for calculating category expense
    public interface CategoryExpenseCallback {
        void onCategoryExpenseCalculated(double categoryExpense);
        void onError(Exception exception);
    }

    // Method to calculate category expense
    public void calculateCategoryExpense(int categoryID, CategoryExpenseCallback callback) {
        double[] categoryExpense = {0}; // Using an array to make it effectively final

        monthExpensesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String expenseAmountStr = document.getString("expense_amount");
                    Long categoryId = document.getLong("category_id");
                    if (expenseAmountStr != null && categoryId!=null && categoryID==categoryId) {
                        double expenseAmount = Double.parseDouble(expenseAmountStr);
                        categoryExpense[0] += expenseAmount;
                    }
                }
                // Now 'totalExpense[0]' contains the sum of all "expense_amount" values
                callback.onCategoryExpenseCalculated(categoryExpense[0]);
            } else {
                // Handle failures
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                    callback.onError(exception);
                }
            }
        });
    }

    // Callback interface for calculating month expense
    public interface MonthExpenseCallback {
        void onMonthExpenseCalculated(double monthExpense);
        void onError(Exception exception);
    }

    // Method to calculate month expense
    public void calculateMonthExpense(CollectionReference collectionReference, MonthExpenseCallback callback) {
        double[] monthExpense = {0}; // Using an array to make it effectively final

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String expenseAmountStr = document.getString("expense_amount");
                    if (expenseAmountStr != null) {
                        double expenseAmount = Double.parseDouble(expenseAmountStr);
                        monthExpense[0] += expenseAmount;
                    }
                }
                // Now 'monthExpense[0]' contains the sum of all "expense_amount" values
                callback.onMonthExpenseCalculated(monthExpense[0]);
            } else {
                // Handle failures
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                    callback.onError(exception);
                }
            }
        });
    }

    // Callback interface for retrieving month budget
    public interface MonthBudgetCallback {
        void onMonthBudgetRetrieved(double budget);
        void onError(Exception exception);
    }

    // Method to get month budget
    public void getMonthBudget(CollectionReference collectionReference, int categoryID, MonthBudgetCallback callback) {
        AtomicReference<Double> budget = new AtomicReference<>((double) 0);
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve budget values for each category
                    Long categoryId = document.getLong("category_id");
                    String value = document.getString("budget_amount");

                    if (categoryId != null && !value.equals("not set") && categoryId == categoryID) {
                        // Set the values in the corresponding EditText views
                        budget.set(Double.parseDouble(value));
                        break;
                    }
                }
                // Call the callback with the retrieved budget
                callback.onMonthBudgetRetrieved(budget.get());
            } else {
                // Handle failures
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                    callback.onError(exception);
                }
            }
        });
    }

}