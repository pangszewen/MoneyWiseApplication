package com.example.moneywise.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneywise.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList; // Use the Expense model class

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        // Set values to TextViews
        holder.categoryNameTextView.setText(expense.getCategoryName());
        holder.descriptionTextView.setText(expense.getDescription());
        holder.amountTextView.setText("RM"+Double.parseDouble(expense.getExpense_amount()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String formattedDate = dateFormat.format(expense.getDate());
        holder.dateTextView.setText(formattedDate);
        switch (expense.getCategory_id().toString()) {
            case "1":
                setDrawableTop(holder.categoryButton, R.drawable.hamburger_burger_svgrepo_com);
                break;
            case "2":
                setDrawableTop(holder.categoryButton, R.drawable.groceries_grocery_svgrepo_com);
                break;
            case "3":
                setDrawableTop(holder.categoryButton, R.drawable.bus_svgrepo_com);
                break;
            case "4":
                setDrawableTop(holder.categoryButton, R.drawable.smartphone_touch_screen_svgrepo_com);
                break;
            case "5":
                setDrawableTop(holder.categoryButton, R.drawable.cinema_screen_cinema_svgrepo_com);
                break;
            case "6":
                setDrawableTop(holder.categoryButton, R.drawable.medical_kit_svgrepo_com);
                break;
            default:
                setDrawableTop(holder.categoryButton, R.drawable.more_circle_horizontal_svgrepo_com);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        // Define UI components for the item view
        TextView categoryNameTextView;
        TextView descriptionTextView;
        TextView amountTextView;
        TextView dateTextView;
        Button categoryButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components here
            categoryNameTextView = itemView.findViewById(R.id.tvCategory);
            descriptionTextView = itemView.findViewById(R.id.tvDesc);
            amountTextView = itemView.findViewById(R.id.tvAmount);
            categoryButton = itemView.findViewById(R.id.btnCategory);
            dateTextView = itemView.findViewById(R.id.tvDate);
        }
    }

    private void setDrawableTop(Button button, int drawableResId) {
        // Set drawable on top of the button text
        button.setCompoundDrawablesWithIntrinsicBounds(0, drawableResId, 0, 0);
    }


}

