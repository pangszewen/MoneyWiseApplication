package com.example.moneywise.login_register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {
    List<FAQ> faqList;
    Context context;

    public FAQAdapter(Context context, List<FAQ> faqList) {
        this.faqList = faqList;
        this.context = context;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FAQ faq = faqList.get(position);
        holder.setFAQ(faq);
        String faqQuestion= faq.getQuestion();
        String originalString = faq.getAnswer();
        StringBuilder rebuiltString = new StringBuilder();

        for (int i = 0; i < originalString.length(); i++) {
            char currentChar = originalString.charAt(i);

            if (currentChar == '\\' && i < originalString.length() - 1 && originalString.charAt(i + 1) == 'n') {
                rebuiltString.append("\n");
                i++;
            } else if(currentChar == '\\' && i < originalString.length() - 1 && originalString.charAt(i + 1) == 't'){
                rebuiltString.append("\t");
                i++;
            }else{
                rebuiltString.append(currentChar);
            }
        }

        String faqAnswer = rebuiltString.toString();

        holder.questionTV.setText(faqQuestion);
        holder.answerTV.setText(faqAnswer);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.answerTV.getVisibility()==View.GONE){
                    holder.answerTV.setVisibility(View.VISIBLE);
                    holder.dropdown_btn.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                } else {
                    holder.answerTV.setVisibility(View.GONE);
                    holder.dropdown_btn.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView questionTV;
        TextView answerTV;
        ImageView dropdown_btn;
        FAQ faq;
        public void setFAQ(FAQ faq) {
            this.faq = faq;
        }
        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTV = itemView.findViewById(R.id.questionTV);
            answerTV = itemView.findViewById(R.id.answerTV);
            dropdown_btn=itemView.findViewById(R.id.dropdown_btn);
        }
    }
}

