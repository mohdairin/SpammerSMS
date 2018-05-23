package com.example.airin.spammersms;

/**
 * Created by airin on 28/03/2018.
 */

public class User {

    private String username, email, gender;

    public User(int id, String username, String email, String gender) {
        this.username = username;
        this.email = email;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }
}