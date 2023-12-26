package com.example.moneywise.login_register;

public class User {
    private String userID;
    private String name;
    private String gender;
    private String dob;
    private String qualification;
    private String role;

    public User(){}

    public User(String userID, String name, String gender, String dob){
        this.userID = userID;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.qualification = null;
        this.role = "Learner";
    }

    public User(String userID, String name, String gender, String dob, String qualification){
        this.userID = userID;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.qualification = qualification;
        this.role = "Advisor";
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
