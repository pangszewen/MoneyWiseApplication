package com.example.moneywise.scholarship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.window.OnBackInvokedDispatcher;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;

public class NewsFullActivity extends AppCompatActivity {

    WebView webView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_full);

        String url = getIntent().getStringExtra("url");
        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);


        back = findViewById(R.id.imageArrowleft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the origin activity from the Intent
                String originActivity = getIntent().getStringExtra("originActivity");

                // Handle the click event, navigate to the appropriate activity
                Intent intent;
                if ("FindNewsActivity".equals(originActivity)) {
                    intent = new Intent(NewsFullActivity.this, FindNewsActivity.class);
                    startActivity(intent);
                } else if ("HomeActivity".equals(originActivity)) {
                    intent = new Intent(NewsFullActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    // Default to FindScholarshipActivity if not specified
                    intent = new Intent(NewsFullActivity.this, FindNewsActivity.class);
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }
}