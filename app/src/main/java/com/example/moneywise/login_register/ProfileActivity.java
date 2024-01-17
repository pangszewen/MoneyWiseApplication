package com.example.moneywise.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    String uid,role;
    ImageButton btn_back;
    RelativeLayout myacc_column,adm_column,logout_column,hs_column,about_column;
    Switch noti_switch;
    SharedPreferences sharedPreferences;
    TextView detail_name,detail_role;
    ImageView gender_icon,profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fStorage= FirebaseStorage.getInstance();
        uid=fAuth.getCurrentUser().getUid();
        btn_back=findViewById(R.id.btn_back);
        detail_name=findViewById(R.id.detail_name);
        detail_role=findViewById(R.id.detail_role);
        gender_icon=(ImageView) findViewById(R.id.gender_icon);
        profile_pic=findViewById(R.id.IV_profilepic);
        myacc_column=findViewById(R.id.myacc_column);
        noti_switch=findViewById(R.id.noti_switch);
        sharedPreferences = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        adm_column=findViewById(R.id.adm_column);
        logout_column=findViewById(R.id.logout_column);
        hs_column=findViewById(R.id.hs_column);
        about_column=findViewById(R.id.about_column);

        displayProfilePic(uid);
        loadDetailFromDB();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        myacc_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyAccActivity.class));
                finish();
            }
        });

        noti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DocumentReference ref= fStore.collection("USER_DETAILS").document(uid);
                Map<String,Object> userDetails=new HashMap<>();
                if (isChecked) {
                    userDetails.put("notification",true);
                } else {
                    userDetails.put("notification",false);
                }
                ref.update(userDetails);
            }
        });

        adm_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdministratorModeActivity.class));
                finish();
            }
        });

        logout_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        hs_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HnSActivity.class));
                finish();
            }
        });

        about_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AboutAppActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for new profile picture URI
        if (getIntent().hasExtra("newProfilePicUri")) {
            String newProfilePicUri = getIntent().getStringExtra("newProfilePicUri");
            // Update profile picture in ImageView
            Picasso.get()
                    .load(newProfilePicUri)
                    .placeholder(R.drawable.profile_pic)
                    .error(R.drawable.profile_pic)
                    .into(profile_pic);
        }
    }

    // Load user details from Firestore and display
    private void loadDetailFromDB(){
        DocumentReference documentReference=fStore.collection("USER_DETAILS").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                role=value.getString("role");
                detail_name.setText(value.getString("name"));
                if(!role.equals("Admin")){
                    adm_column.setVisibility(View.GONE);
                }
                detail_role.setText(role);
                if (value.getString("gender").equals("Male")) {
                    gender_icon.setBackgroundResource(R.drawable.baseline_male_24);
                }
                noti_switch.setChecked(value.getBoolean("notification"));
            }
        });
    }

    // Display user profile picture from Firebase Storage
    private void displayProfilePic(String uid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("USER_PROFILE_PIC").child(uid).child("Profile Pic.jpg"); // Replace with your image file extension
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.profile_pic) // Replace with a placeholder image while loading
                    .into(profile_pic);
        });
    }
}