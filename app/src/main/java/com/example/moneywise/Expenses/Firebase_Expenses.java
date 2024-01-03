package com.example.moneywise.Expenses;

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

    public interface BudgetCallback {
        void onBudgetRetrieved(double budget);
    }

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

    public interface TotalExpenseCallback {
        void onTotalExpenseCalculated(double totalExpense);
        void onError(Exception exception);
    }

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

    public interface CategoryExpenseCallback {
        void onCategoryExpenseCalculated(double categoryExpense);
        void onError(Exception exception);
    }

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

    public interface MonthExpenseCallback {
        void onMonthExpenseCalculated(double monthExpense);
        void onError(Exception exception);
    }

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

    public interface MonthBudgetCallback {
        void onMonthBudgetRetrieved(double budget);
        void onError(Exception exception);
    }

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








    /*
    ArrayList<ForumTopic> forumTopics = new ArrayList<>();


    public void getForumData(){
        forumTopics.clear();
        Log.d("TAG", "getForumData");
        ArrayList<ForumTopic> forumTopics = new ArrayList<>();

        CollectionReference collectionReference = db.collection("FORUM_TOPIC");


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentSnapshot> documents = value.getDocuments();
                for(DocumentSnapshot document : documents){
                    forumTopics.add(document.toObject(ForumTopic.class));
                    Log.d("TAG", Integer.toString(forumTopics.size()));
                }
            }
        });



        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String topicID = document.getId();
                        String userID = document.get("userID").toString();
                        String dateString = document.get("datePosted").toString(); // Replace this with your string
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        LocalDateTime datePosted = LocalDateTime.parse(dateString, formatter);
                        String subject = document.get("subject").toString();
                        String description = document.get("description").toString();

                        // cast the returned Object to Long, then convert it to an int
                        Long likesLong = (Long) document.get("likes");
                        int likes = likesLong != null ? likesLong.intValue() : 0;
                        // Firestore retrieves List objects as List<Object> and not as ArrayList<String>
                        List<Object> commentIDObjects = (List<Object>) document.get("commentID");
                        ArrayList<String> commentID = new ArrayList<>();
                        if (commentIDObjects != null) {
                            for (Object obj : commentIDObjects) {
                                if (obj instanceof String) {
                                    commentID.add((String) obj);
                                }
                            }
                        }
                        ForumTopic topic = new ForumTopic(topicID, userID, datePosted, subject, description, likes, commentID);
                        Log.d("TAG", topic.getTopicID());
                        setForumTopics(topic);
                    }
                }
            }
        });
        Log.d("TAG", "after reading, " + Integer.toString(forumTopics.size()));
    }

    public void setForumTopics(ForumTopic topic){
        Log.d("TAG", "setForumTopics");
        forumTopics.add(topic);
        Log.d("TAG", Integer.toString(forumTopics.size()));
    }

    public ArrayList<ForumTopic> getForumTopics(){
        Log.d("TAG", "getForumTopics");
        Log.d("TAG", Integer.toString(forumTopics.size()));
        return forumTopics;
    }


    public interface ForumDataCallback {
        void onForumDataReceived(ForumTopic forumTopic);
    }




    public void getForumData(final ForumDataCallback callback){
        Log.d("TAG", "getForumData");
        ArrayList<ForumTopic> forumTopics = new ArrayList<>();

        CollectionReference collectionReference = db.collection("FORUM_TOPIC");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("TAG", "task successful");
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Log.d("TAG", documentSnapshot.getData().toString());
                    forumTopics.add(documentSnapshot.toObject(ForumTopic.class));
                    Log.d("TAG", Integer.toString(forumTopics.size()));
                }
                callback.onForumDataReceived(forumTopics);
            }
        });

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("TAG", "complete");
                if (task.isSuccessful()) {
                    Log.d("TAG", "task successful");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        forumTopics.add(document.toObject(ForumTopic.class));
                        Log.d("TAG", Integer.toString(forumTopics.size()));
                    }
                    callback.onForumDataReceived(forumTopics); // Pass the data through the callback
                }
            }
        });

    }
    */
}