package com.example.moneywise.quiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewCoursesAdapter extends RecyclerView.Adapter<NewCoursesAdapter.newCourseViewHolder> {
    private List<Course> courseList;
    private FirebaseStorage storage;
    private Context context;

    public NewCoursesAdapter(Context context, List<Course> courseList) {
        this.courseList = courseList;
        this.context = context;
    }

    @NonNull
    @Override
    public newCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_new_course_display, parent, false);
        return new newCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newCourseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Course course = courseList.get(position);
        String courseTitle = course.getCourseTitle();

        // Setting default image resource
        holder.imageViewCourseCover.setImageResource(R.drawable.outline_image_grey);

        // Firebase storage initialization
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("COURSE_COVER_IMAGE/" + course.getCourseID() + "/");

        // Fetching course cover image from Firebase Storage
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
                                // Checking if the view holder is still bound to the same position
                                if (position == holder.getAdapterPosition()) {
                                    // Loading image into ImageView using Picasso
                                    Picasso.get().load(firstImageUri).into(holder.imageViewCourseCover);
                                }
                            }
                        });
                    }
                }
            }
        });

        // Setting course title
        holder.textViewCourseTitle.setText(courseTitle);

        // Handling item click to navigate to the individual course page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, activity_individual_course_page.class);
                // Passing course information to the next activity
                intent.putExtra("courseID", course.getCourseID());
                intent.putExtra("title", course.getCourseTitle());
                intent.putExtra("description", course.getCourseDesc());
                intent.putExtra("advisorID", course.getAdvisorID());
                intent.putExtra("language", course.getCourseLanguage());
                intent.putExtra("level", course.getCourseLevel());
                intent.putExtra("mode", course.getCourseMode());
                intent.putExtra("previousClass", context.toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Limiting the number of displayed items to a maximum of 3
        return Math.min(courseList.size(), 3);
    }

    public class newCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCourseCover;
        TextView textViewCourseTitle;

        public newCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCourseCover = itemView.findViewById(R.id.IVCourseImage);
            textViewCourseTitle = itemView.findViewById(R.id.TVCourseTitle);
        }
    }
}
