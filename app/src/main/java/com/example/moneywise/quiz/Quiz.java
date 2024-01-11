package com.example.moneywise.quiz;

import java.time.LocalDateTime;

public class Quiz {
    private String quizID;
    private String quizTitle;
    private String advisorID;
    private LocalDateTime dateCreated;
    private String numOfQues;
    private boolean isBookmarked;
    public Quiz(){}
    public Quiz(String quizID, String quizTitle,String advisorID, String numOfQues) {
        this.quizID = quizID;
        this.quizTitle = quizTitle;
        this.advisorID = advisorID;
        this.numOfQues = numOfQues;
        this.dateCreated = LocalDateTime.now();
    }
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    public String getQuizID() {
        return quizID;
    }
    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }
    public String getQuizTitle() {
        return quizTitle;
    }
    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }
    public String getAdvisorID() {
        return advisorID;
    }
    public void setAdvisorID(String advisorID) {
        this.advisorID = advisorID;
    }
    public String getNumOfQues() {return numOfQues;}
    public void setNumOfQues(String numOfQues) {this.numOfQues = numOfQues;}
    public boolean isBookmarked() {return isBookmarked;}
    public void setBookmarked(boolean bookmarked) {isBookmarked = bookmarked;}
}
