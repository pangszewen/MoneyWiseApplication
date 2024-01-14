package com.example.moneywise.login_register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;
import com.example.moneywise.quiz.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApproveQuizAdapter extends RecyclerView.Adapter<ApproveQuizAdapter.ApproveQuizViewHolder>{
    List<Question> quesList;
    Context context;

    public ApproveQuizAdapter(Context context, List<Question> quesList) {
        this.quesList = quesList;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ApproveQuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.approve_quiz_item, parent, false);
        return new ApproveQuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveQuizAdapter.ApproveQuizViewHolder holder, int position) {
        Question ques = quesList.get(position);
        holder.setQuestion(ques);
        String quesText= ques.getQuestionText();

        ArrayList<String> options = new ArrayList<>();
        options.add(ques.getOption1());
        options.add(ques.getOption2());
        options.add(ques.getOption3());
        options.add(ques.getCorrectAns());
        Collections.shuffle(options); // Shuffle the options

        holder.quesNum.setText("Question "+(position+1));
        holder.quesText.setText(quesText);
        holder.option1.setText(options.get(0));
        holder.option2.setText(options.get(1));
        holder.option3.setText(options.get(2));
        holder.option4.setText(options.get(3));

        // Reset background for all options
        holder.A.setBackgroundResource(R.drawable.quiz_button_outline);
        holder.B.setBackgroundResource(R.drawable.quiz_button_outline);
        holder.C.setBackgroundResource(R.drawable.quiz_button_outline);
        holder.D.setBackgroundResource(R.drawable.quiz_button_outline);

        // Highlight the correct answer
        if (options.get(0).equals(ques.getCorrectAns())){
            holder.A.setBackgroundResource(R.drawable.quiz_button_outline_green);
        }
        if (options.get(1).equals(ques.getCorrectAns())){
            holder.B.setBackgroundResource(R.drawable.quiz_button_outline_green);
        }
        if (options.get(2).equals(ques.getCorrectAns())){
            holder.C.setBackgroundResource(R.drawable.quiz_button_outline_green);
        }
        if (options.get(3).equals(ques.getCorrectAns())){
            holder.D.setBackgroundResource(R.drawable.quiz_button_outline_green);
        }
    }

    @Override
    public int getItemCount() {
        return quesList.size();
    }

    public class ApproveQuizViewHolder extends RecyclerView.ViewHolder {
        LinearLayout A,B,C,D;
        TextView quesNum,quesText,option1,option2,option3,option4;
        Question ques;
        public void setQuestion(Question ques) {
            this.ques = ques;
        }
        public ApproveQuizViewHolder(@NonNull View itemView) {
            super(itemView);
            A=itemView.findViewById(R.id.option1);
            B=itemView.findViewById(R.id.option2);
            C=itemView.findViewById(R.id.option3);
            D=itemView.findViewById(R.id.option4);
            quesNum=itemView.findViewById(R.id.quesNum);
            quesText=itemView.findViewById(R.id.quesText);
            option1=itemView.findViewById(R.id.option1_text);
            option2=itemView.findViewById(R.id.option2_text);
            option3=itemView.findViewById(R.id.option3_text);
            option4=itemView.findViewById(R.id.option4_text);
        }
    }
}
