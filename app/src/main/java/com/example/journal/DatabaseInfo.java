package com.example.journal;

import android.provider.BaseColumns;

// Просто данные по бд, какие тут ещё нужны пояснения
final class DatabaseInfo implements BaseColumns {
    public static final String TABLE_NAME = "journal";
    public static final String COLUMN_DISH = "dish";
    public static final String COLUMN_MASS = "mass";
    public static final String COLUMN_EATING = "eating";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_DISH + " TEXT, " +
                    COLUMN_MASS + " INTEGER, " +
                    COLUMN_EATING + " INTEGER, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " TEXT)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private DatabaseInfo() {}
}
