package com.example.moneywise.expenses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Expense {
    private Object category_id;  // Use Object to handle both String and Long
    private String description;
    private String expense_amount;
    private String categoryName;
    private int categoryIconResourceId;
    String date;

    // Default constructor
    public Expense() {
        // Required empty public constructor for Firestore
    }

    // Constructor with parameters
    public Expense(Object category_id, String description, String expense_amount, String categoryName, int categoryIconResourceId) {
        this.category_id = category_id;
        this.description = description;
        this.expense_amount = expense_amount;
        this.categoryName = categoryName;
        this.categoryIconResourceId = categoryIconResourceId;
    }

    // Getters and setters with handling both String and Long
    public Object getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Object category_id) {
        this.category_id = category_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpense_amount() {
        return expense_amount;
    }

    public void setExpense_amount(String expense_amount) {
        this.expense_amount = expense_amount;
    }


    public String getCategoryName() {
        if (category_id instanceof Long) {
            // Convert Long to String if needed
            return convertLongToString((Long) category_id);
        } else if (category_id instanceof String) {
            return convertLongToString(Long.parseLong((String) category_id));
        } else {
            return "Others";
        }
    }

    // Helper method to convert Long to String
    private String convertLongToString(Long longValue) {
        if (longValue == 1) return "Meal";
        else if (longValue == 2) return "Daily";
        else if (longValue == 3) return "Transport";
        else if (longValue == 4) return "Communication";
        else if (longValue == 5) return "Recreation";
        else if (longValue == 6) return "Medical";
        else return "Others";
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryIconResourceId() {
        return categoryIconResourceId;
    }

    public void setCategoryIconResourceId(int categoryIconResourceId) {
        this.categoryIconResourceId = categoryIconResourceId;
    }

    public Date getDate (){
        return setDate(date);
    }

    public Date setDate (String dateS){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date dateNew = null;
        try{
            dateNew = dateFormat.parse(dateS);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return dateNew;
    }

}

