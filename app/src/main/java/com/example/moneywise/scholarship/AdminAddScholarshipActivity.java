package com.example.moneywise.scholarship;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.load.data.DataRewinder;
import com.example.moneywise.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdminAddScholarshipActivity extends AppCompatActivity {

    Random rand = new Random();
    ArrayList<Scholarship> scholarshipArrayList;
    FirebaseFirestore db;
    private TextView txtDeadline;
    private ImageView imageCalendar;
    private Button save;

    private String scholarshipID, title, institution, about, valueOfScho, details, website;
    private EditText ETTitle, ETInsititution, ETAbout, ETValue, ETDetails, ETWebsite;
    private Date deadline;
    private Calendar selectedDate = Calendar.getInstance();
    private int year, month, dayOfMonth, hour, minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_scholarship);
        db = FirebaseFirestore.getInstance();
        scholarshipArrayList = new ArrayList<Scholarship>();
        getAllScholarship();

        txtDeadline = findViewById(R.id.txtDeadline);
        imageCalendar = findViewById(R.id.imageCalendar);

        imageCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog();
            }
        });

        ImageView back = findViewById(R.id.imageArrowleft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddScholarshipActivity.this, FindScholarshipActivity.class);
                startActivity(intent);
            }
        });

        save = findViewById(R.id.btnSave);

        //Save the new created scholarship into Firestore SCHOLARSHIP collection
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ETTitle = findViewById(R.id.ETTitle);
                ETInsititution = findViewById(R.id.ETInsititution);
                ETAbout = findViewById(R.id.ETAbout);
                ETValue = findViewById(R.id.ETValue);
                ETDetails = findViewById(R.id.ETDetails);
                ETWebsite = findViewById(R.id.ETWebsite);

                title = ETTitle.getText().toString();
                institution = ETInsititution.getText().toString();
                about = ETAbout.getText().toString();
                valueOfScho = ETValue.getText().toString();
                details = ETDetails.getText().toString();
                website = ETWebsite.getText().toString();

                // Check if any field is empty
                if (title.isEmpty() || institution.isEmpty() || about.isEmpty() ||
                        valueOfScho.isEmpty() || details.isEmpty() || website.isEmpty()) {
                    // Display an error message or prevent the save action


                    Toast.makeText(AdminAddScholarshipActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }

                deadline = selectedDate.getTime();

                scholarshipID = generateScholarshipID(scholarshipArrayList);
                Scholarship newScho = new Scholarship(scholarshipID,institution, title, about, details, valueOfScho , deadline, website, false);

                insertScholarshipIntoDatabase(newScho);

                // Set all Edit Text back to empty after adding the new scholarship into Firestore
                ETTitle.setText("");
                ETInsititution.setText("");
                ETAbout.setText("");
                ETValue.setText("");
                ETDetails.setText("");
                ETWebsite.setText("");

                selectedDate = Calendar.getInstance();
                updateDeadlineText();
            }
        });

//        selectedDate = Calendar.getInstance();
//        updateDeadlineText();

    }
    // Method to open date picker and call method to open time picker
    private void openDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                year = selectedYear;
                month = selectedMonth;
                dayOfMonth = selectedDayOfMonth;

                openTimeDialog();
            }
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    //Method to open time picker
    private void openTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;

                selectedDate.set(year, month, dayOfMonth, hour, minute);

                updateDeadlineText();
            }
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    // Method called for admin to set deadline, which is open date picker
    private void openDialog() {
        openDateDialog();
    }

    private void updateDeadlineText() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH); // Remove the + 1 here
        int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);

        // Add 1 to the month when displaying it
        txtDeadline.setText(String.format("%02d / %02d / %04d", dayOfMonth, month + 1, year));
    }

    //Method to get the scholarships list from Firestore collection
    private void getAllScholarship() {
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

                                // Add the scholarship to the list
                                scholarshipArrayList.add(scholarship);

                            }
                        }
                    }
                });
    }

    // Method to insert new scholarship to Firestore collection
    private void insertScholarshipIntoDatabase(Scholarship newScho){
        db.collection("SCHOLARSHIP")
                .document(scholarshipID)
                .set(newScho)
                .addOnSuccessListener(aVoid -> {
                    // Document added successfully
                    Toast.makeText(AdminAddScholarshipActivity.this, "Scholarship added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Toast.makeText(AdminAddScholarshipActivity.this, "Error adding scholarship: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Method to generate a new, unique scholarship ID with the format SXXXXXXX
    private String generateScholarshipID(ArrayList<Scholarship> scholarshipArrayList){
        String newID = null;
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = "S" + String.format("%07d", randomNum); //T0001000
            if(checkDuplicatedTopicID(newID, scholarshipArrayList))
                break;
        }
        return newID;
    }

    // Check if the new scholarship ID is duplicated with the existing scholarship list
    private boolean checkDuplicatedTopicID(String newID, ArrayList<Scholarship> scholarshipArrayList) {
        for (Scholarship scholarship : scholarshipArrayList) {
            if (newID.equals(scholarship.getScholarshipID()))
                return false;
        }
        return true;

    }
}