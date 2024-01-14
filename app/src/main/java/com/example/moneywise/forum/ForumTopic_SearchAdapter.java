package com.example.moneywise.forum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.moneywise.R;

import java.util.ArrayList;

public class ForumTopic_SearchAdapter extends ArrayAdapter<ForumTopic> {
    private Context context;
    private ArrayList<ForumTopic> originalList;
    private ArrayList<ForumTopic> filteredList;

    public ForumTopic_SearchAdapter(Context context, ArrayList<ForumTopic> topics) {
        super(context, R.layout.forum_dropdown, topics);
        this.context = context;
        this.originalList = new ArrayList<>(topics);
        this.filteredList = new ArrayList<>(topics);
    }

    public ArrayList<ForumTopic> getFilteredList(){
        return filteredList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.forum_dropdown, parent, false);
        }

        // Display the subject in the AutoCompleteTextView
        ForumTopic topic = getItem(position);
        if (topic != null) {
            TextView subjectTextView = convertView.findViewById(R.id.dropdown_menu);
            subjectTextView.setText(topic.getSubject());
        }

        return convertView;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<ForumTopic> filtered = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(originalList); // Show all items if the constraint is empty
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ForumTopic topic : originalList) {
                        if (topic.getSubject().toLowerCase().contains(filterPattern)) {  // compare the search keywords with topic subject title
                            filtered.add(topic);
                        }
                    }
                }

                results.values = filtered;
                results.count = filtered.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();        // clear the previous results
                addAll((ArrayList<ForumTopic>) results.values);
                notifyDataSetChanged();
                filteredList.clear();
                filteredList.addAll((ArrayList<ForumTopic>) results.values);
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((ForumTopic) resultValue).getSubject();
            }
        };
    }
}
