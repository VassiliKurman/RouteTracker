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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * TrackerContentProvider. Content provider for tracks.
 *
 * Created by Vassili Kurman on 01/08/2018.
 * Version 1.0
 */
public class TrackerContentProvider extends ContentProvider {

    private static final String TAG = TrackerContentProvider.class.getName();

    // Define final integer constants for the directory of recipes and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int TRACKS = 100;
    public static final int WAYPOINTS = 200;
    public static final int TRACK_WITH_ID = 101;
    public static final int WAYPOINTS_WITH_ID = 201;

    // Declaring a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Member variable for a DbHelper that's initialized in the onCreate() method
    private TrackerDbHelper mTrackerDbHelper;

    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add URI matches

        uriMatcher.addURI(TrackerContract.AUTHORITY, TrackerContract.PATH_TRACKS, TRACKS);
        uriMatcher.addURI(TrackerContract.AUTHORITY, TrackerContract.PATH_TRACKS + "/#", TRACK_WITH_ID);
        uriMatcher.addURI(TrackerContract.AUTHORITY, TrackerContract.PATH_WAYPOINTS, WAYPOINTS);
        uriMatcher.addURI(TrackerContract.AUTHORITY, TrackerContract.PATH_WAYPOINTS + "/#", WAYPOINTS_WITH_ID);

        return uriMatcher;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTrackerDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tracks directory
        int match = sUriMatcher.match(uri);
        // URI to be returned
        Uri returnUri;
        long id;
        switch (match) {
            case TRACKS:
                // Insert new values into the database
                id = db.insertWithOnConflict(TrackerContract.TracksEntry.TABLE_NAME_TRACKS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TrackerContract.TracksEntry.CONTENT_URI_TRACKS, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case WAYPOINTS:
                // Insert new values into the database
                id = db.insertWithOnConflict(TrackerContract.WaypointsEntry.TABLE_NAME_WAYPOINTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TrackerContract.WaypointsEntry.CONTENT_URI_WAYPOINTS, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mTrackerDbHelper = new TrackerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mTrackerDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        String id;
        switch (match) {
            // Query for the tracks directory
            case TRACKS:
                Log.d(TAG, "Retrieving all tracks");
                retCursor = db.query(TrackerContract.TracksEntry.TABLE_NAME_TRACKS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRACK_WITH_ID:
                id = uri.getPathSegments().get(1);
                Log.d(TAG, "Retrieving tracks: " + id);
                retCursor = db.query(TrackerContract.TracksEntry.TABLE_NAME_TRACKS,
                        projection,
                        "id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            // Query for the waypoints directory
            case WAYPOINTS:
                Log.d(TAG, "Retrieving all waypoints");
                retCursor = db.query(TrackerContract.WaypointsEntry.TABLE_NAME_WAYPOINTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case WAYPOINTS_WITH_ID:
                id = uri.getPathSegments().get(1);
                Log.d(TAG, "Retrieving waypoints for track: " + id);
                retCursor = db.query(TrackerContract.WaypointsEntry.TABLE_NAME_WAYPOINTS,
                        projection,
                        TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID + "=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }
}