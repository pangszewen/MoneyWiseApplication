package com.example.moneywise.quiz;

public class Lesson {
    private String lessonTitle;
    private String lessonDuration;
    private String lessonUrl;

    public Lesson(String lessonTitle, String lessonDuration, String lessonUrl) {
        this.lessonTitle = lessonTitle;
        this.lessonDuration = lessonDuration;
        this.lessonUrl = lessonUrl;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getLessonDuration() {
        return lessonDuration;
    }
//
    public void setLessonDuration(String lessonDuration) {
        this.lessonDuration = lessonDuration;
    }

    public String getLessonUrl() {
        return lessonUrl;
    }

    public void setLessonUrl(String lessonUrl) {
        this.lessonUrl = lessonUrl;
    }
}

