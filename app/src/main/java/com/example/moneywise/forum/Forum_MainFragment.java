package com.example.moneywise.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moneywise.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Forum_MainFragment extends Fragment {

    RecyclerView RVForum;
    Forum_Adapter forumAdapter;
    Firebase_Forum firebaseForum = new Firebase_Forum();
    Button btn_myTopic, btn_mostRecent, btn_featuredTopics, btn_mostLikes, btn_mostComments;
    Button[] filterButtons;
    ImageButton btn_createTopic, btn_searchIcon;
    ClearableAutoCompleteTextView search_box;
    SwipeRefreshLayout RVForumRefresh;
    ForumTopic_SearchAdapter adapter;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_forum__main, container, false);
        btn_myTopic = rootview.findViewById(R.id.MyTopics);
        btn_mostRecent = rootview.findViewById(R.id.MostRecent);
        btn_featuredTopics = rootview.findViewById(R.id.FeaturedTopics);
        btn_mostLikes = rootview.findViewById(R.id.MostLikes);
        btn_mostComments = rootview.findViewById(R.id.MostComments);
        filterButtons = new Button[]{btn_mostRecent, btn_featuredTopics, btn_mostLikes, btn_mostComments};
        RVForum = rootview.findViewById(R.id.RVForum);
        RVForumRefresh = rootview.findViewById(R.id.RVForumRefresh);
        btn_createTopic = rootview.findViewById(R.id.btn_createTopic);
        btn_searchIcon = rootview.findViewById(R.id.btn_searchIcon);
        search_box = rootview.findViewById(R.id.search_box);
        search_box.setVisibility(View.GONE);
        progressBar = rootview.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        search_box.setThreshold(3);
        setSearchBarAdapter();

        btn_searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSearch(false);
            }
        });

        search_box.setOnClearListener(new ClearableAutoCompleteTextView.OnClearListener() {
            @Override
            public void onClear() {
                toggleSearch(true);
                btn_mostRecent.performClick();
            }
        });

        search_box.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        search_box.dismissDropDown(); // Hide the dropdown
                    }
                    return true; // Consume the event
                }
                return false;
            }
        });


        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                ArrayList<ForumTopic> filteredList = adapter.getFilteredList();
                setRVForumBasedOnSearchResult(filteredList);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ForumTopic selectedTopic = adapter.getItem(position);
                ArrayList<ForumTopic> topics = new ArrayList<>();
                topics.add(selectedTopic);
                setRVForumBasedOnSearchResult(topics);
            }
        });

        btn_myTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Forum_MyTopic_Activity.class));
            }
        });

        btn_mostRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                resetButtonTextColor();
                btn_mostRecent.setTextColor(getResources().getColor(R.color.blue));
                setMostRecentFilter();
            }
        });

        btn_featuredTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                resetButtonTextColor();
                btn_featuredTopics.setTextColor(getResources().getColor(R.color.blue));
                setFeaturedTopicsFilter();
            }
        });

        btn_mostLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                resetButtonTextColor();
                btn_mostLikes.setTextColor(getResources().getColor(R.color.blue));
                setMostLikesFilter();
            }
        });

        btn_mostComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                resetButtonTextColor();
                btn_mostComments.setTextColor(getResources().getColor(R.color.blue));
                setMostCommentsFilter();
            }
        });

        btn_createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Forum_CreateTopic_Activity.class);
                intent.putExtra("class", getActivity().getClass().toString());
                startActivity(intent);
            }
        });

        RVForumRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                btn_mostRecent.performClick();
                setSearchBarAdapter();
                RVForumRefresh.setRefreshing(false);
            }
        });

        btn_mostRecent.performClick();

        return rootview;
    }

    // Method to set up recyclerview of topics that match the search keyword
    public void setRVForumBasedOnSearchResult(ArrayList<ForumTopic> selectedTopic){
        prepareRecyclerView(getActivity(), RVForum, selectedTopic);
    }

    // Method to set up search bar adapter
    public void setSearchBarAdapter(){
        firebaseForum.getForumTopicsInOrder(new Firebase_Forum.ForumTopicInOrderCallback() {
            @Override
            public void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics) {
                adapter = new ForumTopic_SearchAdapter(getActivity(), forumTopics);
                search_box.setAdapter(adapter);
            }
        });
    }

    public void prepareRecyclerView(Context context, RecyclerView RV,   ArrayList<ForumTopic> object) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        RV.setLayoutManager(linearLayoutManager);
        preAdapter(context, RV, object);
    }

    public void preAdapter(Context context, RecyclerView RV, ArrayList<ForumTopic> object) {
        forumAdapter = new Forum_Adapter(context, object);
        RV.setAdapter(forumAdapter);
    }

    // Method triggered when search icon is clicked or cancel button of search bar is clicked
    protected void toggleSearch(boolean reset) {
        if (reset) {
            // hide search box and show search icon
            search_box.setText("");
            search_box.setVisibility(View.GONE);
            btn_searchIcon.setVisibility(View.VISIBLE);
            // hide the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search_box.getWindowToken(), 0);
        } else {
            // hide search icon and show search box
            btn_searchIcon.setVisibility(View.GONE);
            search_box.setVisibility(View.VISIBLE);
            search_box.requestFocus();
            // show the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(search_box, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // Method to display the forum topics in descending order based on their date posted (newest to oldest)
    public void setMostRecentFilter(){
        firebaseForum.getForumTopicsInOrder(new Firebase_Forum.ForumTopicInOrderCallback() {
            @Override
            public void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics) {
                prepareRecyclerView(getActivity(), RVForum, forumTopics);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Method to display the forum topics in descending order based on their total number of likes and comments
    public void setFeaturedTopicsFilter() {
        firebaseForum.getForumTopicsInOrder(new Firebase_Forum.ForumTopicInOrderCallback() {
            @Override
            public void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics) {
                // Sort the forumTopics based on total likes and comments
                Collections.sort(forumTopics, new Comparator<ForumTopic>() {
                    @Override
                    public int compare(ForumTopic topic1, ForumTopic topic2) {
                        int totalLikesAndComments1 = topic1.getLikes().size() + topic1.getCommentID().size();
                        int totalLikesAndComments2 = topic2.getLikes().size() + topic2.getCommentID().size();
                        // Sort in descending order
                        return Integer.compare(totalLikesAndComments2, totalLikesAndComments1);
                    }
                });
                prepareRecyclerView(getActivity(), RVForum, forumTopics);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Method to display the forum topics in descending order based on their total number of likes
    public void setMostLikesFilter() {
        firebaseForum.getForumTopicsInOrder(new Firebase_Forum.ForumTopicInOrderCallback() {
            @Override
            public void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics) {
                // Sort the forumTopics based on total likes
                Collections.sort(forumTopics, new Comparator<ForumTopic>() {
                    @Override
                    public int compare(ForumTopic topic1, ForumTopic topic2) {
                        int totalLikes1 = topic1.getLikes().size();
                        int totalLikes2 = topic2.getLikes().size();
                        // Sort in descending order
                        return Integer.compare(totalLikes2, totalLikes1);
                    }
                });
                prepareRecyclerView(getActivity(), RVForum, forumTopics);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Method to display the forum topics in descending order based on their total number of comments
    public void setMostCommentsFilter() {
        firebaseForum.getForumTopicsInOrder(new Firebase_Forum.ForumTopicInOrderCallback() {
            @Override
            public void onForumTopicsReceived(ArrayList<ForumTopic> forumTopics) {
                // Sort the forumTopics based on total likes and comments
                Collections.sort(forumTopics, new Comparator<ForumTopic>() {
                    @Override
                    public int compare(ForumTopic topic1, ForumTopic topic2) {
                        int totalComments1 = topic1.getCommentID().size();
                        int totalComments2 = topic2.getCommentID().size();
                        // Sort in descending order
                        return Integer.compare(totalComments2, totalComments1);
                    }
                });
                prepareRecyclerView(getActivity(), RVForum, forumTopics);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Method to set the textColor of all buttons to white
    public void resetButtonTextColor(){
        for(Button button : filterButtons){
            button.setTextColor(getResources().getColor(R.color.white));
        }
    }
}