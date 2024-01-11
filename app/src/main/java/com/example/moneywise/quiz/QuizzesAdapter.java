package com.example.moneywise.quiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.QuizViewHolder> implements Filterable {
    List<Quiz> quizList;
    List<Quiz> quizListFull;
    FirebaseFirestore db;
    FirebaseStorage storage;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();

    // Constructor
    public QuizzesAdapter(Context context, List<Quiz> quizList) {
        this.quizList = quizList;
        this.context = context;
        quizListFull = new ArrayList<>(quizList);
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
        String quizTitle = quiz.getQuizTitle();
        String advisorID = quiz.getAdvisorID();
        holder.setQuiz(quiz);

        // Get advisor's name
        db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("USER_DETAILS").document(advisorID);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.textViewAuthorName.setText(documentSnapshot.getString("name"));
            }
        });

        // Load quiz cover image from Firebase Storage
        holder.imageViewQuizCover.setImageResource(R.drawable.outline_image_grey);
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("QUIZ_COVER_IMAGE/" + quiz.getQuizID() + "/");
        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                Log.d("TAG", "complete");
                if (task.isSuccessful()) {
                    Log.d("TAG", "success");
                    List<StorageReference> items = task.getResult().getItems();
                    Log.d("TAG", items.toString());
                    if (!items.isEmpty()) {
                        // Get the first item (image) in the folder
                        items.get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String firstImageUri = uri.toString();
                                if (position == holder.getAdapterPosition()) {
                                    Picasso.get().load(firstImageUri).into(holder.imageViewQuizCover);
                                }
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

        // Set other quiz details
        holder.textViewQuizTitle.setText(quizTitle);
        holder.numOfQues.setText(quiz.getNumOfQues() + " Questions");

        // Open individual quiz page on item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activity_individual_quiz_page.class);
                intent.putExtra("quizID", quiz.getQuizID());
                intent.putExtra("title", quiz.getQuizID());
                intent.putExtra("numOfQues", quiz.getNumOfQues());
                context.startActivity(intent);
            }
        });

        // Toggle bookmark state on bookmark button click
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.toggleBookmark();
            }
        });

        // Set bookmark icon based on the bookmark state
        if (quiz.isBookmarked()) {
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_filled_24);
        }
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    @Override
    public Filter getFilter() {
        return quizFilter;
    }

    // Filter for searching quizzes
    public Filter quizFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Quiz> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(quizListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Quiz quiz : quizListFull) {
                    if (quiz.getQuizTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(quiz);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            quizList.clear();
            quizList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    // ViewHolder for a single quiz item
    public class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewQuizCover;
        TextView textViewQuizTitle;
        TextView textViewAuthorName;
        TextView numOfQues;
        ImageButton bookmark;
        Quiz quiz;

        // Constructor
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewQuizCover = itemView.findViewById(R.id.image_quiz_cover);
            textViewQuizTitle = itemView.findViewById(R.id.text_quiz_title);
            textViewAuthorName = itemView.findViewById(R.id.text_author_name);
            numOfQues = itemView.findViewById(R.id.numOfQues);
            bookmark = itemView.findViewById(R.id.bookmark);

            // Set click listener for the bookmark button
            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleBookmark();
                }
            });
        }

        // Set the current quiz
        public void setQuiz(@NonNull Quiz quiz) {
            this.quiz = quiz;
        }

        // Toggle the bookmark state
        public void toggleBookmark() {
            if (quiz.isBookmarked()) {
                bookmark.setImageResource(R.drawable.baseline_bookmark_filled_24);
                quiz.setBookmarked(false);
                Log.d("Title at toggle true", quiz.getQuizTitle());
                saveBookmarkState(quiz, false);
            } else {
                bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
                quiz.setBookmarked(true);
                Log.d("Title at toggle false", quiz.getQuizTitle());
                saveBookmarkState(quiz, false);
            }
        }
    }

    // Save the bookmark state in Firestore
    private void saveBookmarkState(Quiz quiz, boolean isBookmarked) {
        Map<String, Object> map = new HashMap<>();
        map.put("quizID", quiz.getQuizID());
        map.put("advisorID", quiz.getAdvisorID());
        map.put("title", quiz.getQuizTitle());
        map.put("numOfQues", quiz.getNumOfQues());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bookmarkRef = db.collection("USER_DETAILS")
                .document(userID)
                .collection("QUIZZES_BOOKMARK")
                .document(quiz.getQuizID());
        if (quiz.isBookmarked()) {
            bookmarkRef.delete();
            Log.d(("TAG"), "Delete from database");
        } else {
            bookmarkRef.set(map);
            Log.d(("TAG"), "Save to database");
        }
    }

    // Load bookmarked quizzes from Firestore and update the adapter
    public void loadBookmarkedCourses() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USER_DETAILS")
                .document(userID)
                .collection("QUIZZES_BOOKMARK")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String courseID = documentSnapshot.getId();

                        for (Quiz quiz : quizList) {
                            if (quiz.getQuizID().equals(courseID)) {
                                quiz.setBookmarked(true);
                                break;
                            }
                        }
                    }
                    notifyDataSetChanged();
                });
    }
}
