package com.insightsurface.notebook.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.insightsurface.notebook.bean.NoteBean;

import java.util.ArrayList;


public class DbAdapter {
    public static final String DB_NAME = "notebook.db";
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context context) {
        dbHelper = new DbHelper(context, DB_NAME, null, 1);
        db = dbHelper.getWritableDatabase();
    }

    private void insertNote(String title, String content, String folder) {
        db.execSQL(
                "insert into NOTEBOOK (title,content,folder) values (?,?,?)",
                new Object[]{title, content, folder});
    }

    public boolean isNoteExist(String id) {
        Cursor cursor = db.rawQuery(
                "select * from NOTEBOOK where id=?",
                new String[]{id});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void updateNote(String id, String title, String content, String folder) {
        //初始记录
        if (isNoteExist(id)) {
            db.execSQL("update NOTEBOOK set title=?,content=?,folder=? where id=?",
                    new Object[]{title, content, folder, id});
        } else {
            insertNote(title, content, folder);
        }
    }

    public void deleteNoteById(String id) {
        db.execSQL("delete from NOTEBOOK where id=?",
                new Object[]{id});
    }

    public ArrayList<NoteBean> queryAllNote() {
        ArrayList<NoteBean> resBeans = new ArrayList<NoteBean>();
        Cursor cursor = db
                .query("NOTEBOOK", null, null, null, null, null, "createdtime desc");//asc正序,desc倒序

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String time = cursor.getString(cursor.getColumnIndex("createdtime"));
            String folder = cursor.getString(cursor.getColumnIndex("folder"));
            NoteBean item = new NoteBean();
            item.setDbId(id);
            item.setTitle(title);
            item.setCreate_at_s(time);
            item.setFolder(folder);
            resBeans.add(item);
        }
        cursor.close();
        return resBeans;
    }

    public void closeDb() {
        if (null != db) {
            db.close();
        }
    }
}
