package com.example.moneywise.scholarship;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_MyTopic_Activity;
import com.example.moneywise.home.HomeActivity;
import com.example.moneywise.login_register.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FindScholarshipActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Scholarship> sortedScholarships, scholarshipArrayList;
    ScholarshipAdapter scholarshipAdapter;
    FirebaseFirestore db;

    FirebaseAuth auth;
    FirebaseUser user;

    ImageView back;

    SearchView searchView;
    BottomNavigationView bottomNavigationView;
    String userID;
    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_scholarship);

        auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.imageArrowleft);

        userID = user.getUid();
        addButton = findViewById(R.id.fabAdd);

        // get Role of user + set visibility of the Add button
        getRole(userID);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindScholarshipActivity.this, AdminAddScholarshipActivity.class);
                startActivity(intent);
            }
        });

        ImageView back = findViewById(R.id.imageArrowleft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindScholarshipActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });


        searchView = findViewById(R.id.searchScho);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerScholarshipList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sortedScholarships = new ArrayList<Scholarship>();
        scholarshipArrayList = new ArrayList<Scholarship>();
        scholarshipAdapter = new ScholarshipAdapter(FindScholarshipActivity.this, scholarshipArrayList);

        recyclerView.setAdapter(scholarshipAdapter);

        EventChangeListener();
    }

    private void filterList(String text) {
        ArrayList<Scholarship> filteredList = new ArrayList<>();
        for(Scholarship scholarship : scholarshipArrayList){
            if(scholarship.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    scholarship.getInstitution().toLowerCase().contains(text.toLowerCase()) ||
                    scholarship.getDescription().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(scholarship);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "No scholarship found", Toast.LENGTH_SHORT).show();
        }
        else{
            scholarshipAdapter.setFilteredList(filteredList);
        }
    }

    private void EventChangeListener() {
        db.collection("SCHOLARSHIP")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

//                        sortedScholarships.clear(); // Clear the sorted list

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                DocumentSnapshot documentSnapshot = dc.getDocument();

                                // Use the document ID as the scholarship ID
                                String scholarshipID = documentSnapshot.getId();

                                // Create a Scholarship object and set the scholarship ID
                                Scholarship scholarship = documentSnapshot.toObject(Scholarship.class);
                                scholarship.setScholarshipID(scholarshipID);

                                // Set the deadline from Timestamp
                                scholarship.setDeadlineFromTimestamp(documentSnapshot.getTimestamp("deadline"));


                                // Check if the scholarship is saved by the user
                                checkIfScholarshipIsSaved(scholarship);

                                // Add the scholarship to the sorted list
                                sortedScholarships.add(scholarship);
                            }
                        }

                        // Sort the list based on the deadline
                        Collections.sort(sortedScholarships, new Comparator<Scholarship>() {
                            @Override
                            public int compare(Scholarship s1, Scholarship s2) {
                                return s2.getDeadline().compareTo(s1.getDeadline());
                            }
                        });

                        // Update the main list with the sorted list
                        scholarshipArrayList.clear();
                        scholarshipArrayList.addAll(sortedScholarships);

                        // Notify the adapter that the data set has changed
                        scholarshipAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void checkIfScholarshipIsSaved(final Scholarship scholarship) {

        db.collection("USER_DETAILS")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve the saved_scholarship array from user_details
                            List<String> savedScholarships = (List<String>) documentSnapshot.get("saved_scholarship");

                            // Check if the scholarship ID is in the saved_scholarship array
                            if (savedScholarships != null && savedScholarships.contains(scholarship.getScholarshipID())) {
                                // Set the saved field in the Scholarship object to true
                                scholarship.setSaved(true);
                            } else {
                                // Set the saved field in the Scholarship object to false
                                scholarship.setSaved(false);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore error", e.getMessage());
                    }
                });
    }

    private void getRole(String userID){

        db.collection("USER_DETAILS")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");

                                User user = new User();

                                updateUIBasedOnRole(role);

                            } else {

                                Log.e("Firestore error", "User doesn't exist", task.getException());
                            }
                        } else {
                            Log.e("Firestore error", "Collection not found", task.getException());
                        }
                    }
                });
    }

    private void updateUIBasedOnRole(String role) {

        if ("Admin".equals(role)) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
        }
    }



}

