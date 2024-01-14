package com.example.moneywise.login_register;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    ImageButton btn_back;
    Button btn_send;
    TextView cooldown;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        btn_back=findViewById(R.id.btn_back);
        btn_send=findViewById(R.id.btn_send_ve);
        cooldown=findViewById(R.id.cooldown);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        verifyEmail(user);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyEmail(user);
            }
        });
    }

    // Send email verification to the user
    private void verifyEmail(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Email sent.");
                            Toast.makeText(VerifyEmailActivity.this, "Registered successfully. Please check your email.",
                                    Toast.LENGTH_LONG).show();
                            setCooldown();
                            btn_send.setVisibility(View.GONE);
                            cooldown.setVisibility(View.VISIBLE);
                        }
                        else {
                            Log.e("TAG", "sendEmailVerification", task.getException());
                            Toast.makeText(VerifyEmailActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }}
                });
    }

    // Set cooldown timer for the send button
    private void setCooldown() {
        btn_send.setVisibility(View.GONE);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cooldown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                cooldown.setVisibility(View.GONE);
                btn_send.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}