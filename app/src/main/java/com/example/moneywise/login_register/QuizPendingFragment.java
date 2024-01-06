package com.example.moneywise.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.moneywise.R;
import com.example.moneywise.quiz.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizPendingFragment extends Fragment {

    private RecyclerView recyclerView;
    private PendingQuizAdapter quizAdapter;
    FirebaseFirestore db;
    List<Quiz> quizList;
    SwipeRefreshLayout RVQuizRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_pending_display, container, false);
        db = FirebaseFirestore.getInstance();
        RVQuizRefresh = view.findViewById(R.id.RVQuizRefresh);
        recyclerView = view.findViewById(R.id.quiz_recycle_view);
        setUpRVCourse();

        RVQuizRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRVCourse();
                RVQuizRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    public void setUpRVCourse() {
        quizList = new ArrayList<>();
            CollectionReference coursesRef = db.collection("QUIZ_PENDING");

        coursesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Quiz> listOfCourse = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()){
                    Quiz quiz = convertDocumentToListOfQuiz(dc);
                    listOfCourse.add(quiz);
                }
                quizAdapter = new PendingQuizAdapter(getContext(), listOfCourse);
                prepareRecyclerView(getContext(), recyclerView, listOfCourse);
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Quiz> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Quiz> object){
        quizAdapter = new PendingQuizAdapter(context, object);
        RV.setAdapter(quizAdapter);
    }

    public Quiz convertDocumentToListOfQuiz(QueryDocumentSnapshot dc){
        Quiz quiz = new Quiz();
        quiz.setQuizID(dc.getId());
        quiz.setQuizTitle(dc.get("title").toString());
        quiz.setAdvisorID(dc.get("advisorID").toString());
        quiz.setNumOfQues(dc.get("numOfQues").toString());
        return quiz;
    }
}