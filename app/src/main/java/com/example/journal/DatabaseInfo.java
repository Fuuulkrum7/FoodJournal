package com.example.journal;

import android.provider.BaseColumns;

// Просто данные по бд, какие тут ещё нужны пояснения
final class DatabaseInfo implements BaseColumns {
    public static final String JOURNAL_TABLE = "journal";
    public static final String COLUMN_ID = "local_id";
    public static final String COLUMN_DISH = "dish";
    public static final String COLUMN_MASS = "mass";
    public static final String COLUMN_EATING = "eating";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TIME_ADD = "time_add";
    public static final String USER_TABLE = "user";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_NAME = "username";

    static final String SQL_CREATE_JOURNAL =
            "CREATE TABLE " + JOURNAL_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_DISH + " TEXT, " +
                    COLUMN_MASS + " INTEGER, " +
                    COLUMN_EATING + " INTEGER, " +
                    COLUMN_CALORIES + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_USER_ID + " INTEGER DEFAULT 0, " +
                    COLUMN_TIME_ADD + " TEXT);";

    static final String SQL_CREATE_USER =
            "CREATE TABLE " + USER_TABLE + " (" +
                    COLUMN_USER_ID + " INTEGER DEFAULT 0, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_LOGIN + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT);";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + JOURNAL_TABLE;

    private DatabaseInfo() {}
}
