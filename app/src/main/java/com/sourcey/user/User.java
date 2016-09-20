package com.sourcey.user;

public class User {

    String UserEmail;
    String Name;
    String Password;
    String Email;
    Double Height;
    Double Weight;
    int Age;

    public User() {
    }

    public User(String userEmail, String name, String password, String email, Double height, Double weight, int age) {
        UserEmail = userEmail;
        Name = name;
        Password = password;
        Email = email;
        Height = height;
        Weight = weight;
        Age = age;
    }
    public User(String password, String email) {
        Password = password;
        Email = email;
    }

    public User(String UserEmail) {
        UserEmail = UserEmail;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Double getHeight() {
        return Height;
    }

    public void setHeight(Double height) {
        Height = height;
    }

    public Double getWeight() {
        return Weight;
    }

    public void setWeight(Double weight) {
        Weight = weight;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }
}
