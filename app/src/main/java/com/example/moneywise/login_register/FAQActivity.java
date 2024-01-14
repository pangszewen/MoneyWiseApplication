package com.example.moneywise.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FAQAdapter faqAdapter;
    FirebaseFirestore db;
    List<FAQ> faqList;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.faq_recycle_view);
        backButton = findViewById(R.id.btn_back);
        setUpRVCourse();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HnSActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Initialize and retrieve the list of FAQs from Firestore
    public void setUpRVCourse() {
        faqList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("FAQ");

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<FAQ> listOfFAQ = new ArrayList<>();
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                FAQ topic = convertDocumentToListOfFAQ(dc);
                                listOfFAQ.add(topic);
                            }
                            prepareRecyclerView(FAQActivity.this, recyclerView, listOfFAQ);
                        }
                    }
                });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<FAQ> object) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<FAQ> object) {
        faqAdapter = new FAQAdapter(context, object);
        RV.setAdapter(faqAdapter);
    }

    // Convert a Firestore document snapshot to a FAQ object
    public FAQ convertDocumentToListOfFAQ(QueryDocumentSnapshot dc) {
        FAQ faq = new FAQ();
        faq.setId(dc.getId().toString());
        faq.setQuestion(dc.get("question").toString());
        faq.setAnswer(dc.get("answer").toString());
        return faq;
    }
}