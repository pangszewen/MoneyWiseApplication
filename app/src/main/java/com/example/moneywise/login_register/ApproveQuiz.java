package com.example.moneywise.login_register;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneywise.R;
import com.example.moneywise.quiz.Course;
import com.example.moneywise.quiz.CourseViewpagerAdapter;
import com.example.moneywise.quiz.Question;
import com.example.moneywise.quiz.Quiz;
import com.example.moneywise.quiz.activity_create_quiz;
import com.example.moneywise.quiz.activity_quiz_display;
import com.example.moneywise.quiz.fragment_course_lesson_full;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveQuiz extends AppCompatActivity {

    FirebaseFirestore db;
    String titleText,quizID, advisorID, dateCreated;
    TextView title, advisor;
    ImageView quizCoverImage;
    RecyclerView RVQuiz;
    ArrayList<Question> quesList = new ArrayList<>();
    ImageButton backButton;
    Button approve, reject;
    ApproveQuizAdapter AQAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_quiz);
        db = FirebaseFirestore.getInstance();

        quizID = getIntent().getStringExtra("quizID");

        quizCoverImage = findViewById(R.id.quizImage);
        title = findViewById(R.id.TVQuizTitle);
        advisor = findViewById(R.id.TVAdvisorName);
        RVQuiz = findViewById(R.id.RVQuiz);
        backButton = findViewById(R.id.backButton);
        approve = findViewById(R.id.approveButton);
        reject = findViewById(R.id.rejectButton);

        loadQuestionFromDatabase();
        prepareRecyclerView(ApproveQuiz.this, RVQuiz, quesList);
        displayQuiz();

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApproveDialog();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRejectDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPreviousActivity();
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Question> object) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Question> object) {
        AQAdapter = new ApproveQuizAdapter(context, object);
        RV.setAdapter(AQAdapter);
    }

    private void showApproveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApproveQuiz.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure approve this quiz?");
        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveApproveStatusToDatabase();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveApproveStatusToDatabase() {
        Map<String, Object> map = new HashMap<>();
        map.put("dateCreated", dateCreated);
        map.put("advisorID", advisorID);
        map.put("title", titleText);
        map.put("numOfQues", quesList.size());
        DocumentReference ref=db.collection("QUIZ").document(quizID);
        ref.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    for (Question q:quesList){
                        insertQuesIntoDatabase(ref.collection("QUESTION"),q);
                    }
                    Log.d(TAG, "Course fields saved successfully!");
                    saveRejectStatusToDatabase();
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Approved!", Snackbar.LENGTH_SHORT).show();
                } else {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Fail to Create Quiz", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showRejectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApproveQuiz.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you reject this quiz?");
        builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveRejectStatusToDatabase();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadQuestionFromDatabase() {
        CollectionReference quizRef = FirebaseFirestore.getInstance()
                .collection("QUIZ_PENDING")
                .document(quizID)
                .collection("QUESTION");
        quizRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                Question ques = convertDocumentToListOfQues(dc);
                                quesList.add(ques);
                            }
                        }
                    }
                });
    }

    // insert questions to database
    private void insertQuesIntoDatabase(CollectionReference collectionReference, Question ques) {
        Map<String, Object> map = new HashMap<>();
        map.put("quesText", ques.getQuestionText());
        map.put("correctAns", ques.getCorrectAns());
        map.put("option1", ques.getOption1());
        map.put("option2", ques.getOption2());
        map.put("option3", ques.getOption3());
        collectionReference.document(ques.getQuesID()).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Question uploaded");
                        } else {
                            Log.d("TAG", "Failed");
                        }
                    }
                });
    }

    private void saveRejectStatusToDatabase() {
        DocumentReference quizRef = FirebaseFirestore.getInstance()
                .collection("QUIZ_PENDING")
                .document(quizID);

        quizRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document with quizId: " + quizID + " deleted successfully!");
                    Intent intent = new Intent(ApproveQuiz.this, AdministratorModeActivity.class);
                    startActivity(intent);
                });
    }


    private void displayQuiz() {
        db.collection("QUIZ_PENDING").document(quizID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            titleText = document.getString("title");
                            advisorID = document.getString("advisorID");
                            dateCreated=document.getString("dateCreated");
                            title.setText(titleText);

                            displayAdvisorName(advisorID);
                            displayCoverImage();
                        }
                    }
                });
    }

    private void displayAdvisorName(String advisorID) {
        db.collection("USER_DETAILS").document(advisorID)
                .get()
                .addOnCompleteListener(advisorTask -> {
                    if (advisorTask.isSuccessful() && advisorTask.getResult() != null) {
                        DocumentSnapshot advisorDocument = advisorTask.getResult();
                        if (advisorDocument.exists()) {
                            String advisorName = advisorDocument.getString("name");
                            advisor.setText(advisorName);
                        }
                    }
                });
    }

    private void displayCoverImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("QUIZ_COVER_IMAGE/" + quizID + "/");
        storageReference.child("Cover Image.jpeg").getDownloadUrl().addOnSuccessListener(uri -> { // Need remove jpeg
            Log.d("Cover Image", "Successful");
        }).addOnFailureListener(exception -> {
        });
    }

    public void backToPreviousActivity() {
        Intent intent = new Intent(ApproveQuiz.this, AdministratorModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public Question convertDocumentToListOfQues(QueryDocumentSnapshot dc) {
        Question ques = new Question();
        ques.setQuesID(dc.getId().toString());
        ques.setQuestionText(dc.get("quesText").toString());
        ques.setOption1(dc.get("option1").toString());
        ques.setOption2(dc.get("option2").toString());
        ques.setOption3(dc.get("option3").toString());
        ques.setCorrectAns(dc.get("correctAns").toString());
        return ques;
    }
}