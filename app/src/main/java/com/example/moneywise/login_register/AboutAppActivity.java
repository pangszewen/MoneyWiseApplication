package com.example.moneywise.login_register;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.moneywise.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

public class AboutAppActivity extends AppCompatActivity {

    ImageButton btn_back;
    TextView aboutapp_content;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        aboutapp_content=findViewById(R.id.aboutapp_content);
        btn_back =findViewById(R.id.btn_back);
        fStore=FirebaseFirestore.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                finish();
            }
        });

        loadAboutAppFromDB();
    }

    // Load about app from Firestore database and display
    private void loadAboutAppFromDB(){
        DocumentReference documentReference=fStore.collection("ABOUT_APP").document("AboutApp");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String aboutapp=value.getString("aboutapp");
                StringBuilder rebuiltString = new StringBuilder();

                // Replace escape characters in the answer with actual formatting
                for (int i = 0; i < aboutapp.length(); i++) {
                    char currentChar = aboutapp.charAt(i);

                    if (currentChar == '\\' && i < aboutapp.length() - 1 && aboutapp.charAt(i + 1) == 'n') {
                        rebuiltString.append("\n");
                        i++;
                    } else if(currentChar == '\\' && i < aboutapp.length() - 1 && aboutapp.charAt(i + 1) == 't'){
                        rebuiltString.append("\t");
                        i++;
                    }else{
                        rebuiltString.append(currentChar);
                    }
                }

                aboutapp = rebuiltString.toString();
                aboutapp_content.setText(aboutapp);
            }
        });
    }
}