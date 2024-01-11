package com.example.moneywise.login_register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.example.moneywise.quiz.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PendingQuizAdapter extends RecyclerView.Adapter<PendingQuizAdapter.QuizViewHolder> {
    List<Quiz> quizList;
    FirebaseFirestore db;
    FirebaseStorage storage;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();

    public PendingQuizAdapter(Context context, List<Quiz> quizList) {
        this.quizList = quizList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_quiz_display, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Quiz quiz = quizList.get(position);
        holder.setQuiz(quiz);
        String courseTitle = quiz.getQuizTitle();
        String advisorID = quiz.getAdvisorID();

        db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("USER_DETAILS").document(advisorID); // Need change
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.textViewAuthorName.setText(documentSnapshot.getString("name"));
            }
        });
        holder.imageViewQuizCover.setImageResource(R.drawable.outline_image_grey);
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("QUIZ_COVER_IMAGE/" + quiz.getQuizID()+"/");
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    if (!items.isEmpty()) {
                        items.get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String firstImageUri = uri.toString();
                                if (position == holder.getAdapterPosition()) {
                                    Picasso.get().load(firstImageUri).into(holder.imageViewQuizCover);
                                }
                            }
                        });
                    }
                }
            }
        });
        holder.textViewQuizTitle.setText(courseTitle);
        holder.numOfQues.setText(quiz.getNumOfQues()+" Questions");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdmin = new Intent(context, ApproveQuiz.class);
                intentAdmin.putExtra("quizID", quiz.getQuizID());
                context.startActivity(intentAdmin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewQuizCover;
        TextView textViewQuizTitle;
        TextView textViewAuthorName;
        TextView numOfQues;
        ImageButton bookmark;
        Quiz quiz;

        public void setQuiz(Quiz quiz) {
            this.quiz = quiz;
        }

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewQuizCover = itemView.findViewById(R.id.image_quiz_cover);
            textViewQuizTitle = itemView.findViewById(R.id.text_quiz_title);
            textViewAuthorName = itemView.findViewById(R.id.text_author_name);
            numOfQues = itemView.findViewById(R.id.numOfQues);
            bookmark = itemView.findViewById(R.id.bookmark);

            bookmark.setVisibility(View.INVISIBLE);
        }
    }
}
