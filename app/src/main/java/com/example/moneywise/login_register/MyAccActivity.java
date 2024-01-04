package com.example.moneywise.login_register;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MyAccActivity extends AppCompatActivity {

    ImageButton btn_back,btn_change_pic;
    ImageView profileImageView;
    TextView detail_name,detail_email, btn_update;
    TextInputEditText editTextname,editTextemail,editTextDOB;
    TextInputLayout editLayoutDOB;
    AutoCompleteTextView spinner_gender;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    String uid;
    Uri profilepic_uri;
    ArrayAdapter<CharSequence> g_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_acc);
        mAuth=FirebaseAuth.getInstance();
        btn_back=findViewById(R.id.btn_back);
        btn_change_pic=findViewById(R.id.btn_change_pic);
        profileImageView=findViewById(R.id.IV_profilepic);
        detail_name=findViewById(R.id.detail_name);
        detail_email=findViewById(R.id.detail_email);
        btn_update =findViewById(R.id.btn_update);
        editTextname=findViewById(R.id.editTextName);
        editTextemail=findViewById(R.id.editTextEmail);
        editLayoutDOB=findViewById(R.id.EditLayoutDOB);
        editTextDOB=findViewById(R.id.editTextDOB);
        fStore=FirebaseFirestore.getInstance();
        fStorage=FirebaseStorage.getInstance();
        uid=mAuth.getCurrentUser().getUid();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                finish();
            }
        });

        displayProfilePic(uid);
        loadDetailsFromDB();

        spinner_gender=findViewById(R.id.gender_spinner);
        g_adapter=ArrayAdapter.createFromResource(
                this,
                R.array.genders,
                R.layout.dropdown_item
        );
        spinner_gender.setAdapter(g_adapter);
        spinner_gender.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MyAccActivity.this,spinner_gender.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        editLayoutDOB.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dob=editTextDOB.getText().toString();
                String[] dateParts = dob.split("/");
                int y = Integer.parseInt(dateParts[0]);
                int m = Integer.parseInt(dateParts[1]);
                int d = Integer.parseInt(dateParts[2]);
                DatePickerDialog dialog=new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDOB.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                }, y, m, d);

                dialog.show();
            }
        });

        btn_change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                pickImageFromGallery();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editTextname.getText().toString();
                String gender=spinner_gender.getText().toString();
                String dob=editTextDOB.getText().toString();
                if (name.isEmpty()||gender.isEmpty()||dob.isEmpty()){
                    Toast.makeText(MyAccActivity.this,"One or Many field are empty.",Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadProfilePicture(profilepic_uri);
                DocumentReference ref= fStore.collection("USER_DETAILS").document(uid);
                Map<String,Object> userDetails=new HashMap<>();
                userDetails.put("name",name);
                userDetails.put("gender",gender);
                userDetails.put("dob",dob);
                ref.update(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MyAccActivity.this,"Profile updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    private void loadDetailsFromDB(){
        String email=mAuth.getCurrentUser().getEmail();
        detail_email.setText(email);
        editTextemail.setText(email);
        DocumentReference documentReference=fStore.collection("USER_DETAILS").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String name=value.getString("name");
                detail_name.setText(name);
                editTextname.setText(name);
                String selectedGender = value.getString("gender");
                //int position = g_adapter.getPosition(selectedGender);
                //if (position != -1) {
                    //spinner_gender.setListSelection(position);
                //}
                spinner_gender.setText(selectedGender,false);
                editTextDOB.setText(value.getString("dob"));
            }
        });
    }

    private void changeProfilePic(Uri profilePicUri){
        profileImageView.setImageURI(profilePicUri);
        profilepic_uri=profilePicUri;
    }

    private void displayProfilePic(String uid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("USER_PROFILE_PIC").child(uid).child("Profile Pic.jpg"); // Replace with your image file extension
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.profile_pic) // Replace with a placeholder image while loading
                    .error(R.drawable.profile_pic) // Replace with an error image if download fails
                    .into(profileImageView);
        });
    }

    private void uploadProfilePicture(Uri profilePictureUri) {
        if(profilePictureUri!=null) {
            StorageReference reference = fStorage.getReference().child("USER_PROFILE_PIC").child(uid);
            StorageReference imageName = reference.child("Profile Pic" + ".jpg");
            imageName.putFile(profilePictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("TAG", "Profile picture uploaded");
                }
            });
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            if (data.getClipData().getItemCount() > 1) {
                Toast.makeText(this, "Please select only one image", Toast.LENGTH_SHORT).show();
            } else {
                Uri imageUri = data.getClipData().getItemAt(0).getUri();
                changeProfilePic(imageUri);
            }
        }
    }

    private void checkPermission(){
        int check = ContextCompat.checkSelfPermission(MyAccActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(check!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MyAccActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }else{
            pickImageFromGallery();
        }
    }
}