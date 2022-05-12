package com.example.journal;

public class User {
    private String login;
    private String password;
    private String username;
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

