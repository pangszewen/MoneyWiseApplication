package com.example.moneywise.scholarship;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ScholarshipAdapter extends RecyclerView.Adapter<ScholarshipAdapter.ScholarshipViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Context context;
    private ArrayList<Scholarship> scholarshipArrayList;

    // Constructor for the ScholarshipAdapter
    public ScholarshipAdapter(Context context, ArrayList<Scholarship> list) {
        this.context = context;
        this.scholarshipArrayList = list;
    }

    // Method to update the scholarship list based on search or filter
    public void setFilteredList(ArrayList<Scholarship> filteredList) {
        this.scholarshipArrayList = filteredList;
        notifyDataSetChanged();
    }

    // Creating a ViewHolder to represent each item in the RecyclerView
    @NonNull
    @Override
    public ScholarshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout for each scholarship item
        View v = LayoutInflater.from(context).inflate(R.layout.scholarship_item, parent, false);
        return new ScholarshipViewHolder(v);
    }

    // Binding data to each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ScholarshipViewHolder holder, int position) {
        // Getting the scholarship at the specified position
        Scholarship scholarship = scholarshipArrayList.get(position);

        // Setting the title with the institution for each scholarship
        holder.title.setText(scholarship.getTitle() + " | " + scholarship.getInstitution());

        // Set the time format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the deadline
        String utcDeadlineString = sdf.format(scholarship.getDeadline());

        // Set the formatted deadline to the TextView
        holder.deadline.setText(utcDeadlineString);

        // Set a placeholder image for each scholarship
        holder.image.setImageResource(R.drawable.img_schorecyclerview);

        // Handling the click event for each scholarship item
        holder.scholarshipCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent to navigate to the ApplyScholarship activity
                Intent intent = new Intent(v.getContext(), ApplyScholarship.class);

                // Passing scholarship details to the intent
                intent.putExtra("scholarshipID", scholarship.getScholarshipID());
                intent.putExtra("institution", scholarship.getInstitution());
                intent.putExtra("title", scholarship.getTitle());
                intent.putExtra("description", scholarship.getDescription());
                intent.putExtra("criteria", scholarship.getCriteria());
                intent.putExtra("award", scholarship.getAward());
                intent.putExtra("deadline", scholarship.getDeadline());
                intent.putExtra("website", scholarship.getWebsite());
                intent.putExtra("saved", scholarship.isSaved());

                // Determining the origin activity and passing it to the intent
                if (v.getContext() instanceof FindScholarshipActivity) {
                    intent.putExtra("originActivity", "FindScholarshipActivity");
                } else if (v.getContext() instanceof BookmarkActivity) {
                    intent.putExtra("originActivity", "BookmarkActivity");
                }

                // Starting the ApplyScholarship activity with the intent
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        return scholarshipArrayList.size();
    }

    public static class ScholarshipViewHolder extends RecyclerView.ViewHolder {

        TextView title, deadline;

        ImageView image;
        CardView scholarshipCard;

        public ScholarshipViewHolder(@NonNull View itemView) {
            super(itemView);

            // Finding and assigning UI components to variables
            image = itemView.findViewById(R.id.imageScho);
            title = itemView.findViewById(R.id.txtTitle);
            deadline = itemView.findViewById(R.id.txtDeadline);
            scholarshipCard = itemView.findViewById(R.id.scholarshipCard);
        }
    }
}