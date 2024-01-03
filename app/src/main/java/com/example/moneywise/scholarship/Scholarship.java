package com.example.moneywise.scholarship;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Scholarship {

    String scholarshipID, institution, title, description, criteria, award, website;
    Date deadline;
    boolean saved;
    public Scholarship() {
    }

    public Scholarship(String scholarshipID, String institution, String title, String description, String criteria, String award, Date deadline, String website, boolean saved) {
        this.scholarshipID = scholarshipID;
        this.institution = institution;
        this.title = title;
        this.description = description;
        this.criteria = criteria;
        this.award = award;
        this.deadline = deadline;
        this.website = website;
        this.saved = saved;
    }
    @Exclude
    public String getScholarshipID() {
        return scholarshipID;
    }

    @Exclude
    public void setScholarshipID(String scholarshipID) {
        this.scholarshipID = scholarshipID;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setDeadlineFromTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            this.deadline = timestamp.toDate();
        }
    }
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Exclude
    public boolean isSaved() {
        return saved;
    }

    @Exclude
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

}



