package com.example.moneywise.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.Expenses.BookFragment;
import com.example.moneywise.Expenses.Firebase_Expenses;
import com.example.moneywise.R;
import com.example.moneywise.forum.Firebase_Forum;
import com.example.moneywise.login_register.Firebase_User;
import com.example.moneywise.login_register.ProfileActivity;

import com.example.moneywise.login_register.User;

import com.example.moneywise.quiz.Course;
import com.example.moneywise.quiz.CoursesAdapter;
import com.example.moneywise.quiz.CoursesCompletedContinueAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    ImageButton profile;
    TextView welcome, budgetTV, expenseTV, differenceTV, monthTV, TVContinueCourse;
    ConstraintLayout ExpensesView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    Firebase_Expenses firebaseExpenses = new Firebase_Expenses();
    Firebase_User firebaseUser = new Firebase_User();
    Timestamp timestamp = Timestamp.now();
    Date currentDate = new Date(timestamp.getSeconds() * 1000); // Convert seconds to milliseconds
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
    String formattedDate = dateFormat.format(currentDate);
    String userID;
    List<Course> courseList;
    CompleteCourseAdapter completeCourseAdapter;
    ArrayList<Article> articleList = new ArrayList<>();
    LatestNewsAdapter adapter;
    RecyclerView RVLatestNews, RVContinueCourse;
    SwipeRefreshLayout RVLatestNewsRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNews(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        userID = user.getUid();
        welcome=rootview.findViewById(R.id.TVWelcome);
        budgetTV = rootview.findViewById(R.id.TVAmountBudget);
        expenseTV = rootview.findViewById(R.id.TVAmountExpenses);
        differenceTV = rootview.findViewById(R.id.TVAmountBalance);
        monthTV = rootview.findViewById(R.id.TVMonth);
        ExpensesView = rootview.findViewById(R.id.ExpensesView);
        TVContinueCourse = rootview.findViewById(R.id.TVContinueCourse);
        profile=rootview.findViewById(R.id.IBProfile);
        RVLatestNews = rootview.findViewById(R.id.RVLatestNews);
        RVLatestNewsRefresh = rootview.findViewById(R.id.RVLatestNewsRefresh);
        RVContinueCourse =rootview.findViewById(R.id.RVContinueCourse);
        setExpensesView();
        setRVLatestNews();
        setUpRVCourse();

        firebaseUser.getUser(userID, new Firebase_User.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                welcome.setText("Welcome, " + user.getName());
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

        ExpensesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).expensesOnClick();
            }
        });

        RVLatestNewsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRVLatestNews();
                RVLatestNewsRefresh.setRefreshing(false);
            }
        });

        return rootview;
    }

    public void setExpensesView(){
        monthTV.setText(formattedDate);
        firebaseExpenses.getBudget(0, new Firebase_Expenses.BudgetCallback() {
            @Override
            public void onBudgetRetrieved(double budget) {
                // Assign the retrieved budget to the array
                //budget = retrievedBudget;

                double newBudget;

                if (budget % 1 == 0) {
                    // Convert to int if the decimal part is zero
                    newBudget = (int) budget;
                    budgetTV.setText("RM" + Integer.toString((int)newBudget));
                    if (budget==0){
                        budgetTV.setText("Not Set");
                    }
                } else {
                    newBudget = budget;
                    budgetTV.setText("RM" + Double.toString(newBudget));
                }
                firebaseExpenses.calculateTotalExpense(new Firebase_Expenses.TotalExpenseCallback() {
                    double newExpense;
                    @Override
                    public void onTotalExpenseCalculated(double totalExpense) {
                        if (totalExpense % 1 == 0) {
                            // Convert to int if the decimal part is zero
                            newExpense = (int) totalExpense;
                            expenseTV.setText("RM" + Integer.toString((int)newExpense));
                        } else {
                            newExpense = totalExpense;
                            expenseTV.setText("RM" + Double.toString(newExpense));
                        }

                        // Calculate the difference using the array
                        double difference = newBudget - newExpense;

                        if (difference % 1 == 0) {
                            // Convert to int if the decimal part is zero
                            int newDifference = (int) difference;
                            differenceTV.setText("RM" + Integer.toString(newDifference));
                        } else {
                            differenceTV.setText("RM" + Double.toString(difference));
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        // Handle the error, e.g., log it or display a message
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    public void setRVLatestNews(){
        RVLatestNews.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LatestNewsAdapter(articleList);
        RVLatestNews.setAdapter(adapter);
        getNews(null);
    }

    public void setUpRVCourse(){
        courseList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("USER_DETAILS").document(userID).collection("COURSES_JOINED");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Course> listOfCourse = new ArrayList<>();
                for(QueryDocumentSnapshot dc : task.getResult()){
                    Course topic = convertDocumentToListOfCourse(dc);
                    listOfCourse.add(topic);
                }
                if(listOfCourse.isEmpty()){
                    TVContinueCourse.setVisibility(View.GONE);
                    RVContinueCourse.setVisibility(View.GONE);
                }else{
                    TVContinueCourse.setVisibility(View.VISIBLE);
                    RVContinueCourse.setVisibility(View.VISIBLE);
                }
                completeCourseAdapter = new CompleteCourseAdapter(getContext(), listOfCourse);
                prepareRecyclerView(getContext(), RVContinueCourse, listOfCourse);
            }
        });
    }

    public void getNews(String query) {

        NewsApiClient newsApiClient = new NewsApiClient("d4c7f8fe18694e589bd30e86e04a908e");
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("en")
                        .category("business")
                        .q(query)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        articleList = (ArrayList<Article>) response.getArticles();
                        adapter.updateData(articleList);
                        adapter.notifyDataSetChanged();
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                    articleList = (ArrayList<Article>) response.getArticles();
//                                    adapter.updateData(articleList);
//                                    adapter.notifyDataSetChanged();
//                            }
//                        });
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT FAILURE", throwable.getMessage());
                    }
                }
        );
    }

    public void prepareRecyclerView(Context context, RecyclerView RV, List<Course> object){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, List<Course> object){
        completeCourseAdapter = new CompleteCourseAdapter(context, object);
        RV.setAdapter(completeCourseAdapter);
    }

    public Course convertDocumentToListOfCourse(QueryDocumentSnapshot dc){
        Course course = new Course();
        course.setCourseID(dc.getId());
        course.setCourseTitle(dc.get("title").toString());
        course.setAdvisorID(dc.get("advisorID").toString());
//        if (dc.get("description").toString()!=null) {
//            course.setCourseDesc(dc.get("description").toString());
//            course.setCourseLanguage(dc.get("language").toString());
//            course.setCourseLevel(dc.get("level").toString());
//            course.setCourseMode(dc.get("mode").toString());
//        }
        return course;
    }
}