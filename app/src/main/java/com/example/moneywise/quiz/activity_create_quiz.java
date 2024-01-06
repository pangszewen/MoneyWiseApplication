package com.example.moneywise.quiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class activity_create_quiz extends AppCompatActivity {
    String quizID, quesID;
    String quizTitle, advisorID;
    String quesText, quesCorrectAns, quesOption1, quesOption2, quesOption3;
    Uri imageUri;
    ArrayList<Uri> chooseImageList;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storage;;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Integer quesNum = 1;
    ArrayList<Question> listOfQues;
    Boolean pickImage = false;
    private static final int REQUEST_CODE = 2;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        advisorID = user.getUid();

        TextInputEditText title = findViewById(R.id.titleInput);
        TextInputEditText QuesquesText = findViewById(R.id.questionTextInput);
        TextInputEditText correctAns = findViewById(R.id.correctAnsInput);
        TextInputEditText option1 = findViewById(R.id.option1Input);
        TextInputEditText option2 = findViewById(R.id.option2Input);
        TextInputEditText option3 = findViewById(R.id.option3Input);
        FloatingActionButton addQues = findViewById(R.id.addQuesButton);
        Button saveButton = findViewById(R.id.saveButton);
        ImageButton addQuizImage = findViewById(R.id.addQuizImage);
        TextView numOfQues = findViewById(R.id.quesNum);
        ImageButton cancel = findViewById(R.id.cancelButton);

        listOfQues = new ArrayList<>();
        chooseImageList = new ArrayList<>();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // add questions to quiz
        addQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quesText = QuesquesText.getText().toString();
                quesCorrectAns = correctAns.getText().toString();
                quesOption1 = option1.getText().toString();
                quesOption2 = option2.getText().toString();
                quesOption3 = option3.getText().toString();

                quesID = generateQuesID(listOfQues);
                Question newQues = new Question(quesID, quesText, quesCorrectAns, quesOption1, quesOption2, quesOption3);
                listOfQues.add(newQues);

                QuesquesText.getText().clear();
                correctAns.getText().clear();
                option1.getText().clear();
                option2.getText().clear();
                option3.getText().clear();

                quesNum++;
                numOfQues.setText(quesNum+" Question(s)");
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quesText = QuesquesText.getText().toString();
                quesCorrectAns = correctAns.getText().toString();
                quesOption1 = option1.getText().toString();
                quesOption2 = option2.getText().toString();
                quesOption3 = option3.getText().toString();

                quesID = generateQuesID(listOfQues);
                Question newQues = new Question(quesID, quesText, quesCorrectAns, quesOption1, quesOption2, quesOption3);
                listOfQues.add(newQues);

                if (!listOfQues.isEmpty() && pickImage) {
                    quizTitle = title.getText().toString();
                    createQuiz(quizTitle);
                }

                if (!pickImage){
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Please select a Cover Image!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        // add quiz image
        addQuizImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                pickImageFromGallery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
        }
        if (data.getClipData() != null) {
            int itemCount = data.getClipData().getItemCount();
            for (int i = 0; i < itemCount; i++) {
                imageUri = data.getClipData().getItemAt(i).getUri();
                chooseImageList.add(imageUri);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImage = true;
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int check = ContextCompat.checkSelfPermission(activity_create_quiz.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(check!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity_create_quiz.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }else{
                pickImageFromGallery();
            }
        }else{
            pickImageFromGallery();
        }
    }

    // create quiz
    private void createQuiz(String quizTitle) {
        Log.d("TAG", "CreateQuiz");
        CollectionReference collectionReference = db.collection("QUIZ");
        collectionReference.orderBy("dateCreated", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Quiz> quizList = new ArrayList<>();
                    for (QueryDocumentSnapshot dc : task.getResult()) {
                        Quiz quiz = convertDocumentToQuiz(dc);
                        quizList.add(quiz);
                    }
                    quizID = generateQuizID(quizList);
                    Quiz newQuiz = new Quiz(quizID, quizTitle, advisorID, quesNum.toString());
                    insertQuizIntoDatabase(newQuiz);
                    uploadImages(newQuiz.getQuizID());
                    for (Question question : listOfQues) {
                        createQues(question.getQuestionText(), question.getCorrectAns(), question.getOption1(), question.getOption2(), question.getOption3());
                    }
                } else {
                    Toast.makeText(activity_create_quiz.this, "Failed to fetch quizzes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImages(String quizID) {
        for(int i =0; i<chooseImageList.size(); i++){
            Uri image = chooseImageList.get(i);
            if(image!=null){
                StorageReference reference = storage.getReference().child("QUIZ_COVER_IMAGE").child(quizID);
                StorageReference imageName = reference.child("Cover Image.jpeg");
                imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
                });
            }
        }
    }

    private void createQues(String quesText, String quesCorrectAns, String quesOption1, String quesOption2, String quesOption3){
        CollectionReference collectionReference = db.collection("QUIZ_PENDING").document(quizID).collection("QUESTION");
            quesID = generateQuesID(listOfQues);
            Question newQues = new Question(quesID, quesText, quesCorrectAns, quesOption1, quesOption2, quesOption3);
            insertQuesIntoDatabase(collectionReference, newQues);
    }

    // create quiz in database
    private void insertQuizIntoDatabase(Quiz quiz) {
        Map<String, Object> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = quiz.getDateCreated().format(formatter);
        map.put("dateCreated", formattedDateTime);
        map.put("advisorID", quiz.getAdvisorID());
        map.put("title", quiz.getQuizTitle());
        map.put("numOfQues", quiz.getNumOfQues());
        db.collection("QUIZ_PENDING").document(quiz.getQuizID()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Quiz Created!", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity_create_quiz.this, activity_quiz_display.class);
                    startActivity(intent);
                } else {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Fail to Create Quiz", Snackbar.LENGTH_SHORT).show();
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

    private Quiz convertDocumentToQuiz(QueryDocumentSnapshot dc) {
        Quiz quiz = new Quiz();
        quiz.setQuizID(dc.getId());
        quiz.setAdvisorID(dc.get("advisorID").toString());
        quiz.setQuizTitle(dc.get("title").toString());
        return quiz;
    }

    private String generateQuizID(ArrayList<Quiz> quiz) {
        String newID;
        Random rand = new Random();
        while(true) {
            int randomNum = rand.nextInt(1000000);
            newID = "Q" + String.format("%07d", randomNum); //Q0001000
            if(checkDuplicatedQuizID(newID, quiz))
                break;
        }
        Log.d("TAG", "This is new quizID " + newID);
        return newID;
    }

    // create unique questionID
    private String generateQuesID(ArrayList<Question> ques) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;
        Random rand = new Random();
        StringBuilder newID = new StringBuilder();
        while (true) {
            for (int i = 0; i < length; i++) {
                int index = rand.nextInt(characters.length());
                newID.append(characters.charAt(index));
            }
            String generatedID = newID.toString();
            if (checkDuplicatedQuesID(generatedID, ques)) {
                break;
            } else {
                newID.setLength(0);
            }
        }
        Log.d("TAG", "This is new quesID " + newID.toString());
        return newID.toString();
    }

    private boolean checkDuplicatedQuizID(String newID, ArrayList<Quiz> quiz){
        for(Quiz topic: quiz){
            if(newID.equals(topic.getQuizID()))
                return false;
        }
        Log.d("TAG", "This is checked topic ID " + newID);
        return true;
    }

    private boolean checkDuplicatedQuesID(String newID, ArrayList<Question> ques){
        for(Question topic: ques){
            if(newID.equals(topic.getQuesID()))
                return false;
        }
        Log.d("TAG", "This is checked topic ID " + newID);
        return true;
    }
}

// QUIZ -> QUIZID -> QUES(COLLECTION) -> QUESID -> quesText, correctAns, option1-3
//                   title, advisorID
