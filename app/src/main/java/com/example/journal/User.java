package com.example.journal;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;

    @SerializedName("username")
    private String username;

    @SerializedName("id")
    private String id;

    public User(String login, String password, String username, String id) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

