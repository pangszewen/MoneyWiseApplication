package com.example.moneywise.login_register;

import com.example.moneywise.forum.Firebase_Forum;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase_User {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("USER_DETAILS");

    public void getUser(String userID, Firebase_Forum.UserCallback callback){
        DocumentReference userDocRef = userRef.document(userID);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = convertDocumentToUser(documentSnapshot);
                callback.onUserReceived(user);
            }
        });
    }

    public User convertDocumentToUser(DocumentSnapshot dc){
        User user = new User();
        user.setUserID(dc.getId().toString());
        user.setName(dc.get("name").toString());
        user.setGender(dc.get("gender").toString());
        user.setDob(dc.get("dob").toString());
        if(dc.get("qualification")!=null)
            user.setQualification(dc.get("qualification").toString());
        user.setRole(dc.get("role").toString());

        return user;
    }
}
