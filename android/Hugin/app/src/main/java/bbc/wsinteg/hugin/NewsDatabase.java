package bbc.wsinteg.hugin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cablej01 on 06/12/2016.
 */

public class NewsDatabase extends SQLiteOpenHelper {
    public NewsDatabase(Context context) {
        super(context, "News", null, 1);
    }
    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Items (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR, " +
                "body VARCHAR, " +
                "message_id VARCHAR, " +
                "received DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );
    }
    // This method is called when database is upgraded like
    // modifying the table structure,
    // adding constraints to database, etc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addItem(String message_id, String title, String body) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT count(*) FROM Items WHERE title = ?", new String[]{title});
        c.moveToFirst();
        int n = c.getInt(0);
        if(n>0) {
            db.execSQL("UPDATE Items SET body = ? WHERE title = ?", new String[]{body, title});
        }
        else {
            db.execSQL("INSERT INTO Items (message_id, body, title) VALUES(?,?,?)", new String[]{message_id, body, title});
        }
    }

    Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT _id, title, body FROM Items", null);
    }

    Cursor retrieve(String message_id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT title, body FROM Items WHERE message_id=?", new String[]{message_id});
    }
}
