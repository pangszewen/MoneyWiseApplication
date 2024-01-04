package com.example.moneywise.login_register;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;
import com.example.moneywise.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    String uid;
    ImageButton btn_back;
    RelativeLayout myacc_column,theme_column,adm_column,logout_column,hs_column,about_column;
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
        theme_column=findViewById(R.id.theme_column);
        noti_switch=findViewById(R.id.noti_switch);
        sharedPreferences = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        adm_column=findViewById(R.id.adm_column);
        logout_column=findViewById(R.id.logout_column);
        hs_column=findViewById(R.id.hs_column);
        about_column=findViewById(R.id.about_column);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        loadProfilePicFromDB();
        loadDetailFromDB();

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
                    Toast.makeText(ProfileActivity.this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    userDetails.put("notification",false);
                    Toast.makeText(ProfileActivity.this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
                }
                ref.update(userDetails);
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

    private void loadProfilePicFromDB(){
        StorageReference storageReference = fStorage.getReference("USER_PROFILE_PIC" + uid);
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    if (!items.isEmpty()) {
                        items.get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profile_pic.setImageURI(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure if needed
                            }
                        });
                    }
                }
            }
        });
    }

    private void loadDetailFromDB(){
        DocumentReference documentReference=fStore.collection("USER_DETAILS").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                detail_name.setText(value.getString("name"));
                detail_role.setText(value.getString("role"));
                if (value.getString("gender").equals("Male")) {
                    gender_icon.setImageResource(R.drawable.baseline_male_24);
                }
                noti_switch.setChecked(value.getBoolean("notification"));
            }
        });
    }
}