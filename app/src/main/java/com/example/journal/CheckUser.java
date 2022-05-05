package com.example.journal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.NoSuchAlgorithmException;

class CheckUser extends Thread {
    SQLiteDatabase db;
    String login;
    String password;
    LoginFragment loginFragment;

    public CheckUser(SQLiteDatabase db, String login, String password, LoginFragment loginFragment) {
        this.db = db;
        this.login = login;
        this.password = password;
        this.loginFragment = loginFragment;
    }

    @Override
    public void run() {
        Cursor cursor = DatabaseInterface.find_user(login, db);
        if (cursor == null) {
            return;
        }

        // Узнаем индекс каждого столбца
        int passwordColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_PASSWORD);
        cursor.moveToNext();
        String new_password = "";
        if (cursor.getCount() != 0) {
            new_password = cursor.getString(passwordColumnIndex);
        }

        try {
            loginFragment.onUserFound(DatabaseInterface.getSSH512(new_password).equals(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            loginFragment.onUserFound(false);
        }
    }
}
