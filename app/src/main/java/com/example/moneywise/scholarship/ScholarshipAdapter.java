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

    public ScholarshipAdapter(Context context, ArrayList<Scholarship> list) {
        this.context = context;
        this.scholarshipArrayList = list;
    }

    public void setFilteredList(ArrayList<Scholarship> filteredList) {
        this.scholarshipArrayList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScholarshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.scholarship_item, parent, false);
        return new ScholarshipViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScholarshipViewHolder holder, int position) {
        Scholarship scholarship = scholarshipArrayList.get(position);
        holder.title.setText(scholarship.getTitle() + " | " + scholarship.getInstitution());

        // Set the time zone to UTC
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the deadline as a string with UTC time zone
        String utcDeadlineString = sdf.format(scholarship.getDeadline());


        holder.deadline.setText(utcDeadlineString);

        holder.image.setImageResource(R.drawable.img_schorecyclerview);

        holder.scholarshipCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ApplyScholarship.class);
                intent.putExtra("scholarshipID", scholarship.getScholarshipID());
                intent.putExtra("institution", scholarship.getInstitution());
                intent.putExtra("title", scholarship.getTitle());
                intent.putExtra("description", scholarship.getDescription());
                intent.putExtra("criteria", scholarship.getCriteria());
                intent.putExtra("award", scholarship.getAward());
                intent.putExtra("deadline", scholarship.getDeadline());
                intent.putExtra("website", scholarship.getWebsite());
                intent.putExtra("saved", scholarship.isSaved());

                if (v.getContext() instanceof FindScholarshipActivity) {
                    intent.putExtra("originActivity", "FindScholarshipActivity");
                } else if (v.getContext() instanceof BookmarkActivity) {
                    intent.putExtra("originActivity", "BookmarkActivity");
                }

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

            image = itemView.findViewById(R.id.imageScho);
            title = itemView.findViewById(R.id.txtTitle);
            deadline = itemView.findViewById(R.id.txtDeadline);
            scholarshipCard = itemView.findViewById(R.id.scholarshipCard);
        }
    }
}