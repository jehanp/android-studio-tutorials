package com.bignerdranch.android.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s ( _id integer primary key autoincrement, %s, %s, %s, %s, %s, %s, %s)",
                CrimeTable.NAME, CrimeTable.Cols.UUID, CrimeTable.Cols.TITLE, CrimeTable.Cols.DATE, CrimeTable.Cols.SOLVED, CrimeTable.Cols.SUSPECT, CrimeTable.Cols.SUSPECT_CONTACT, CrimeTable.Cols.POLICE_REQUIRED);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
