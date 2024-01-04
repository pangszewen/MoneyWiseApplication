package com.example.moneywise.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    ImageButton profile;
    TextView welcome, budgetTV, expenseTV, differenceTV, monthTV;
    FirebaseAuth auth;
    FirebaseUser user;
    Firebase_Expenses firebaseExpenses = new Firebase_Expenses();
    Firebase_User firebaseUser = new Firebase_User();
    Timestamp timestamp = Timestamp.now();
    Date currentDate = new Date(timestamp.getSeconds() * 1000); // Convert seconds to milliseconds
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMyyyy", Locale.US);
    String formattedDate = dateFormat.format(currentDate);

    String userID;
    ArrayList<Article> articleList = new ArrayList<>();
    LatestNewsAdapter adapter;
    RecyclerView RVLatestNews;
    SwipeRefreshLayout RVLatestNewsRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNews(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        //userID = user.getUid();
        userID = "Zqa2pZRzccPx13bEjxZho9UVlT83";
        welcome=rootview.findViewById(R.id.TVWelcome);
        budgetTV = rootview.findViewById(R.id.TVAmountBudget);
        expenseTV = rootview.findViewById(R.id.TVAmountExpenses);
        differenceTV = rootview.findViewById(R.id.TVAmountBalance);
        monthTV = rootview.findViewById(R.id.TVMonth);
        profile=rootview.findViewById(R.id.IBProfile);
        RVLatestNews = rootview.findViewById(R.id.RVLatestNews);
        RVLatestNewsRefresh = rootview.findViewById(R.id.RVLatestNewsRefresh);
        setExpensesView();
        setRVLatestNews();

        firebaseUser.getUser(userID, new Firebase_Forum.UserCallback() {
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
}