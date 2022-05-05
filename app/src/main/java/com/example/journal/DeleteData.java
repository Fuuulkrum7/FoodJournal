package com.example.journal;

import android.database.sqlite.SQLiteDatabase;

class DeleteData extends Thread {
    int id;
    SQLiteDatabase db;
    String table;
    String key;

    public DeleteData(int id, String table, String key, SQLiteDatabase db) {
        this.db = db;
        this.table = table;
        this.id = id;
        this.key = key;
    }

    @Override
    public void run() {
        db.delete(table, key + "=?", new String[]{Integer.toString(id)});
        db.close();
    }
}
