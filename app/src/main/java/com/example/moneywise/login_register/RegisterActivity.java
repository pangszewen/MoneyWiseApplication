package com.example.moneywise.login_register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextname,editTextemail,editTextpassword,editTextcpassword,editTextDOB;
    TextInputLayout editLayoutDOB;
    AutoCompleteTextView spinner_gender,spinner_role;
    TextView signIn;
    Button btn_reg;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        editTextname=findViewById(R.id.editTextName);
        editTextemail=findViewById(R.id.editTextEmail);
        editTextpassword=findViewById(R.id.editTextPassword);
        editTextcpassword=findViewById(R.id.editTextCPassword);
        editLayoutDOB=findViewById(R.id.EditLayoutDOB);
        editTextDOB=findViewById(R.id.editTextDOB);
        signIn =findViewById(R.id.signIn);
        btn_reg=findViewById(R.id.btn_signup);
        progressBar=findViewById(R.id.progressBar);
        fstore=FirebaseFirestore.getInstance();

        spinner_gender=findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> g_adapter=ArrayAdapter.createFromResource(
                this,
                R.array.genders,
                R.layout.dropdown_item
        );
        spinner_gender.setAdapter(g_adapter);
        spinner_gender.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RegisterActivity.this,spinner_gender.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        spinner_role=findViewById(R.id.role_spinner);
        ArrayAdapter<CharSequence> r_adapter=ArrayAdapter.createFromResource(
                this,
                R.array.roles,
                R.layout.dropdown_item
        );
        spinner_role.setAdapter(r_adapter);
        spinner_role.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RegisterActivity.this,spinner_role.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        editLayoutDOB.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog=new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDOB.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                }, y, m, d);

                dialog.show();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name,gender,DOB,email,role,password,cpassword;
                name=editTextname.getText().toString();
                gender=spinner_gender.getText().toString();
                DOB=editTextDOB.getText().toString();
                email=editTextemail.getText().toString();
                role=spinner_role.getText().toString();
                password=editTextpassword.getText().toString();
                cpassword=editTextcpassword.getText().toString();

                if (TextUtils.isEmpty(name)){
                    editTextname.setError("Name field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(gender)){
                    editTextname.setError("Gender field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    editTextname.setError("Email field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(DOB)){
                    editTextname.setError("Date of birth field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    editTextname.setError("Password field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(cpassword)){
                    editTextname.setError("Please confirm your password");
                    return;
                }
                if (!password.equals(cpassword)){
                    editTextname.setError("Password does not match");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    progressBar.setVisibility(View.GONE);
                                    uid=mAuth.getCurrentUser().getUid();
                                    saveUserDetails(uid,name,gender,DOB,role);
                                    startActivity(new Intent(getApplicationContext(),VerifyEmailActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressBar.setVisibility(View.GONE);
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void saveUserDetails(String uid,String name,String gender,String DOB,String role) {
        DocumentReference documentReference=fstore.collection("USER_DETAILS").document(uid);
        Map<String,Object> userdetails=new HashMap<>();
        userdetails.put("name",name);
        userdetails.put("gender",gender);
        userdetails.put("dob",DOB);
        userdetails.put("role",role);
        documentReference.set(userdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "User info saved");
            }
        });
    }



}
