package com.example.moneywise.scholarship;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_MyTopic_Activity;
import com.example.moneywise.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BookmarkActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    String userID;

    RecyclerView recyclerView;
    ArrayList<Scholarship> sortedBookmarkList, bookmarkArrayList;
    ScholarshipAdapter bookmarkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);


        auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        userID = user.getUid();

        recyclerView = findViewById(R.id.recyclerBookmarkList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sortedBookmarkList = new ArrayList<Scholarship>();
        bookmarkArrayList = new ArrayList<Scholarship>();
        bookmarkAdapter = new ScholarshipAdapter(BookmarkActivity.this, bookmarkArrayList);

        recyclerView.setAdapter(bookmarkAdapter);

        EventChangeListener();

        ImageView back = findViewById(R.id.imageArrowleft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookmarkActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });




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

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                DocumentSnapshot documentSnapshot = dc.getDocument();

                                // Use the document ID as the scholarship ID
                                String scholarshipID = documentSnapshot.getId();

                                // Create a Scholarship object and set the scholarship ID
                                Scholarship scholarship = documentSnapshot.toObject(Scholarship.class);
                                scholarship.setScholarshipID(scholarshipID);

                                // Check if the scholarship is saved by the user
                                checkIfScholarshipIsSaved(scholarship);

                            }
                        }

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

                                scheduleNotification(scholarship);

                                // Add the scholarship to the sorted list
                                sortedBookmarkList.add(scholarship);

                                // Sort the list based on the deadline
                                Collections.sort(sortedBookmarkList, new Comparator<Scholarship>() {
                                    @Override
                                    public int compare(Scholarship s1, Scholarship s2) {
                                        return s2.getDeadline().compareTo(s1.getDeadline());
                                    }
                                });

                                // Update the main list with the sorted list
                                bookmarkArrayList.clear();
                                bookmarkArrayList.addAll(sortedBookmarkList);

                                // Notify the adapter that the data set has changed
                                bookmarkAdapter.notifyDataSetChanged();

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

    private void scheduleNotification(Scholarship scholarship) {
        // Calculate the time difference between the current time and the scholarship deadline
        long currentTimeMillis = System.currentTimeMillis();
        long deadlineMillis = scholarship.getDeadline().getTime();
        long oneDayInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        long timeDifference = deadlineMillis - currentTimeMillis;

        // If the time difference is approximately one day, schedule an FCM notification
        if (timeDifference > 0 && timeDifference <= oneDayInMillis) {
            // Schedule your FCM notification
            String notificationTitle = "Scholarship Deadline Reminder";
            String notificationBody = "The deadline for '" + scholarship.getTitle() + "' is tomorrow.";
        }
    }






}