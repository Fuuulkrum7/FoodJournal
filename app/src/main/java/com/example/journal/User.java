package com.example.journal;

import static com.example.journal.RemoteDatabaseInterface.LOGIN_FIELD;
import static com.example.journal.RemoteDatabaseInterface.PASSWORD_FIELD;
import static com.example.journal.RemoteDatabaseInterface.USERNAME_FIELD;

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

    public User(String login, String password, String username){
        this.login = login;
        this.password = password;
        this.username = username;
    }

    public String getQuery(){
        if (id == null){
            return String.format(
                    "%s=%s&%s=%s&%s=%s",
                    USERNAME_FIELD,
                    username,
                    LOGIN_FIELD,
                    login, PASSWORD_FIELD, password);
        }

        return null;
    }
}

