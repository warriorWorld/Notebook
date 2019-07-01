package com.insightsurface.notebook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private Context xcontext;
    public static final String NOTE_BOOK = "create table if not exists NOTEBOOK ("
            + "id integer primary key autoincrement,"
            + "title text," + "content text," + "folder text,"
            + "createdtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))";

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        xcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NOTE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists NOTEBOOK");
        db.execSQL(NOTE_BOOK);
    }
}
