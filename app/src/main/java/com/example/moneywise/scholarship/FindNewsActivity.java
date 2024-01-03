package com.example.moneywise.scholarship;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moneywise.R;
import com.example.moneywise.forum.Forum_MyTopic_Activity;
import com.example.moneywise.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

public class FindNewsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    ArrayList<Article> articleList = new ArrayList<>();
    NewsRecyclerAdapter adapter;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_news);


        searchView = findViewById(R.id.searchNews);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getNews(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerNewsList);

        setUpRecyclerView();
        getNews(null);




        ImageView back = findViewById(R.id.imageArrowleft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindNewsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

//    private void filterList(String text) {
//        ArrayList<Article> filteredList = new ArrayList<>();
//        for(Article article : articleList){
//            if(article.getTitle().toLowerCase().contains(text.toLowerCase()) ||
//                    article.getDescription().toLowerCase().contains(text.toLowerCase())){
//                filteredList.add(article);
//            }
//        }
//        if(filteredList.isEmpty()){
//            Toast.makeText(this, "No article found", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            adapter.setFilteredList(filteredList);
//        }
//    }


    void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsRecyclerAdapter(articleList);
        recyclerView.setAdapter(adapter);
    }



    void getNews(String query) {

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

                       runOnUiThread(()->{
                           articleList = (ArrayList<Article>) response.getArticles();
                           adapter.updateData(articleList);
                           adapter.notifyDataSetChanged();
                       });
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT FAILURE", throwable.getMessage());
                    }
                }
            );
        }
}
