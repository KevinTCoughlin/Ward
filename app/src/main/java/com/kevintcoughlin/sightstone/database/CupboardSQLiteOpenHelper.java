package com.kevintcoughlin.sightstone.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevintcoughlin.sightstone.models.Champion;
import com.kevintcoughlin.sightstone.models.Summoner;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public final class CupboardSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ward.db";
    private static final int DATABASE_VERSION = 1;

    static {
        cupboard().register(Summoner.class);
        cupboard().register(Champion.class);
    }

    public CupboardSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}