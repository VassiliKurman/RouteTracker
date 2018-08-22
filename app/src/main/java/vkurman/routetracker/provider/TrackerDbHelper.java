/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vkurman.routetracker.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import vkurman.routetracker.provider.TrackerContract.*;

/**
 * TrackerDbHelper
 *
 * Created by Vassili Kurman on 01/08/2018.
 * Version 1.0
 */
public class TrackerDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "TrackerDbHelper";

    /**
     * The database name
     */
    private static final String DATABASE_NAME = "tracks.db";
    /**
     * If the database schema changes, than the database version needs to be incremented
     */
    private static final int DATABASE_VERSION = 3;

    // Constructor
    TrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold the track data
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UsersEntry.TABLE_NAME_USERS+ " (" +
                UsersEntry.COLUMN_USERS_ID + " TEXT NOT NULL," +
                UsersEntry.COLUMN_USERS_NAME + " TEXT)";

        // Create a table to hold the track data
        final String SQL_CREATE_TRACKS_TABLE = "CREATE TABLE " + TracksEntry.TABLE_NAME_TRACKS + " (" +
                TracksEntry.COLUMN_TRACKS_ID + " INTEGER NOT NULL PRIMARY KEY," +
                TracksEntry.COLUMN_TRACKS_NAME + " TEXT NOT NULL, " +
                TracksEntry.COLUMN_TRACKS_OWNER + " TEXT NOT NULL, " +
                TracksEntry.COLUMN_TRACKS_OWNER_NAME + " TEXT NOT NULL, " +
                TracksEntry.COLUMN_TRACKS_IMAGE + " TEXT)";

        // Create a table to hold the waypoint data
        final String SQL_CREATE_WAYPOINTS_TABLE = "CREATE TABLE " + WaypointsEntry.TABLE_NAME_WAYPOINTS + " (" +
                WaypointsEntry.COLUMN_WAYPOINTS_ID + " INTEGER NOT NULL PRIMARY KEY," +
                WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID + " INTEGER NOT NULL, " +
                WaypointsEntry.COLUMN_WAYPOINTS_LATITUDE + " REAL NOT NULL, " +
                WaypointsEntry.COLUMN_WAYPOINTS_LONGITUDE + " REAL NOT NULL, " +
                WaypointsEntry.COLUMN_WAYPOINTS_ALTITUDE + " REAL NOT NULL, " +
                WaypointsEntry.COLUMN_WAYPOINTS_TIMESTAMP + " INTEGER NOT NULL)";

        try {
            sqLiteDatabase.execSQL(SQL_CREATE_USERS_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TRACKS_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_WAYPOINTS_TABLE);
        } catch (SQLException e) {
            Log.e(TAG, "SQL error creating tables: " + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For this project simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WaypointsEntry.TABLE_NAME_WAYPOINTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TracksEntry.TABLE_NAME_TRACKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME_USERS);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For this project simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WaypointsEntry.TABLE_NAME_WAYPOINTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TracksEntry.TABLE_NAME_TRACKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME_USERS);

        onCreate(sqLiteDatabase);
    }
}