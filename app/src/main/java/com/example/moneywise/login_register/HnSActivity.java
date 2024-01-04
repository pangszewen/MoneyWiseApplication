package com.example.moneywise.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.moneywise.R;

public class HnSActivity extends AppCompatActivity {

    ImageButton back_btn;
    RelativeLayout ug_column,faq_column,fp_column, cu_column;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hnsactivity);

        ug_column=findViewById(R.id.ug_column);
        faq_column=findViewById(R.id.faq_column);
        fp_column=findViewById(R.id.fp_column);
        cu_column =findViewById(R.id.cu_column);
        back_btn=findViewById(R.id.btn_back);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });

        ug_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserGuideActivity.class));
                finish();
            }
        });

        faq_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FAQActivity.class));
                finish();
            }
        });

        fp_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgetPasswordActivity.class));
            }
        });

        cu_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ContactUsActivity.class));
                finish();
            }
        });
    }
}