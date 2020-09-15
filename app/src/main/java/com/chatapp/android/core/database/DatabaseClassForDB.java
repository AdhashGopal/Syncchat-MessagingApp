package com.chatapp.android.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseClassForDB extends SQLiteOpenHelper {

    private final static String DB_Name = "chatapp.db";
    private final static String Table_Name = "chatappTABLE_STATUS";
    private final static int DB_Version = 2;
    private final static String UID = "_id";
    private final static String STATUS = "status";
    private final static String CREATE_TABLE = "CREATE TABLE " + Table_Name + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT," + STATUS + " VARCHAR(140));";
    private final static String DROP_TABLE = "DROP TABLE " + Table_Name + ";";
    private final Context context;

    public DatabaseClassForDB(Context context) {
        super(context, DB_Name, null, DB_Version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);

        onCreate(db);
    }

    public void insetData(String status) {
        //String[] value = { "Battery about to die", "At work", "At the movies", "At school", "Busy", "Available" };
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS, status);

        database.insert(Table_Name, null, values);

		/*for(int i=0;i<value.length;i++){            
            values.put(STATUS, value[i]);
			database.insert(Table_Name,null,values); //Insert each time for loop count            
	    }*/
        database.close();
    }

    public ArrayList<String> displayStatusList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<String>();
        String[] column = {UID, STATUS};
        Cursor cursor = db.query(Table_Name, column, null, null, null, null, UID + " DESC");

        while (cursor.moveToNext()) {
            //int uid=cursor.getInt(0);
            String status = cursor.getString(1);
            list.add(status);
        }
        cursor.close();
        return list;
    }

    public void deleteStatus(String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = STATUS + " = " + status;
        //String query ="DELETE FROM "+Table_Name+" WHERE STATUS = "+status+";";
        db.delete(Table_Name, where, null);
        //db.delete(Table_Name, STATUS + "=" + status, null);
        db.close();
    }

    public void deleteAllStatus() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_Name, null, null);
        db.close();
    }

    public boolean isAlreadyInsertedStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        if (status.contains("'")) {
            status = status.replace("'", "''");
        }

        String query = "SELECT * FROM " + Table_Name + " WHERE " + STATUS + "='" + status + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                return true;
            }
            cursor.close();
        }

        return false;
    }




}
