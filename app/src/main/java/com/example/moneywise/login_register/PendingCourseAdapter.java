package com.example.moneywise.login_register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.moneywise.quiz.Course;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PendingCourseAdapter extends RecyclerView.Adapter<PendingCourseAdapter.CourseViewHolder> {
    List<Course> courseList;
    List<Course> courseListFull;
    FirebaseFirestore db;
    FirebaseStorage storage;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userID = user.getUid();

    public PendingCourseAdapter(Context context, List<Course> courseList) {
        this.courseList = courseList;
        this.context = context;
        courseListFull = new ArrayList<>(courseList);
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_course_display, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Course course = courseList.get(position);
        holder.setCourse(course);
        String courseTitle = course.getCourseTitle();
        String advisorID = course.getAdvisorID();

        db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("USER_DETAILS").document(advisorID); // Need change
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.textViewAuthorName.setText(documentSnapshot.getString("name"));
            }
        });
        holder.imageViewCourseCover.setImageResource(R.drawable.outline_image_grey);
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("COURSE_COVER_IMAGE/" + course.getCourseID() + "/");
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
                                    Picasso.get().load(firstImageUri).into(holder.imageViewCourseCover);
                                }
                            }
                        });
                    }
                }
            }
        });
        holder.textViewCourseTitle.setText(courseTitle);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdmin = new Intent(context, ApproveCourse.class);
                intentAdmin.putExtra("courseID", course.getCourseID());
                intentAdmin.putExtra("title", course.getCourseTitle());
                intentAdmin.putExtra("description", course.getCourseDesc());
                intentAdmin.putExtra("advisorID", course.getAdvisorID());
                intentAdmin.putExtra("language", course.getCourseLanguage());
                intentAdmin.putExtra("level", course.getCourseLevel());
                intentAdmin.putExtra("mode", course.getCourseMode());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String formattedDateTime = course.getDateCreated().format(formatter);
                intentAdmin.putExtra("dateCreated",formattedDateTime);
                intentAdmin.putExtra("previousClass", CoursePendingFragment.class.toString());
                context.startActivity(intentAdmin);
            }
        });
        holder.bookmark.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCourseCover;
        TextView textViewCourseTitle;
        TextView textViewAuthorName;
        ImageButton bookmark;
        Course course;
        public void setCourse(Course course) {
            this.course = course;
        }
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCourseCover = itemView.findViewById(R.id.image_quiz_cover);
            textViewCourseTitle = itemView.findViewById(R.id.text_course_title);
            textViewAuthorName = itemView.findViewById(R.id.text_author_name);
            bookmark = itemView.findViewById(R.id.button_bookmark);
        }
    }
}


