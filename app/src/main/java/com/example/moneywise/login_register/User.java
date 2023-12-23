package com.example.moneywise.login_register;

public class User {
    private String userID;
    private String name;
    private String gender;
    private int age;
    private String qualification;
    private String role;

    public User(){}

    public User(String userID, String name, String gender, int age){
        this.userID = userID;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.qualification = null;
        this.role = "Learner";
    }

    public User(String userID, String name, String gender, int age, String qualification){
        this.userID = userID;
        this.name = name;
        this.gender = gender;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAge(Long age){
        this.age = age != null ? age.intValue() : 0;
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
